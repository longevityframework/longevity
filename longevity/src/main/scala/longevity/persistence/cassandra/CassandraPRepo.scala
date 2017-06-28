package longevity.persistence.cassandra

import com.datastax.driver.core.PreparedStatement
import com.datastax.driver.core.Row
import com.datastax.driver.core.Session
import com.typesafe.scalalogging.LazyLogging
import longevity.config.PersistenceConfig
import longevity.context.Effect
import longevity.emblem.emblematic.traversors.sync.EmblematicToJsonTranslator
import longevity.emblem.emblematic.traversors.sync.JsonToEmblematicTranslator
import longevity.emblem.exceptions.CouldNotTraverseException
import longevity.emblem.stringUtil.camelToUnderscore
import longevity.emblem.stringUtil.typeName
import longevity.exceptions.persistence.NotInDomainModelTranslationException
import longevity.model.DerivedPType
import longevity.model.ModelType
import longevity.model.PType
import longevity.model.PolyPType
import longevity.model.realized.RealizedPrimaryKey
import longevity.model.realized.RealizedPropComponent
import longevity.persistence.PRepo
import longevity.persistence.PState
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import typekey.TypeKey
import typekey.typeKey

/** a Cassandra repository for persistent entities of type `P`.
 *
 * @param pType the type of the persistent entities this repository handles
 * @param modelType the model type containing the persistent that this repo persists
 * @param sessionInfo the connection to the cassandra database
 * @param persistenceConfig persistence configuration that is back end agnostic
 */
private[longevity] class CassandraPRepo[F[_], M, P] private (
  effect: Effect[F],
  modelType: ModelType[M],
  pType: PType[M, P],
  protected val persistenceConfig: PersistenceConfig,
  protected val session: () => Session)
extends PRepo[F, M, P](effect, modelType, pType)
with CassandraSchema[F, M, P]
with CassandraCreate[F, M, P]
with CassandraRetrieve[F, M, P]
with CassandraQuery[F, M, P]
with CassandraUpdate[F, M, P]
with CassandraDelete[F, M, P]
with LazyLogging {

  protected[cassandra] val tableName = camelToUnderscore(typeName(pTypeKey.tpe))

  protected val partitionComponents = realizedPType.primaryKey match {
    case Some(key) => key.partitionProps.flatMap {
      _.realizedPropComponents: Seq[RealizedPropComponent[P, _, _]]
    }
    case None => Seq.empty
  }

  protected val postPartitionComponents = realizedPType.primaryKey match {
    case Some(key) => key.postPartitionProps.flatMap {
      _.realizedPropComponents: Seq[RealizedPropComponent[P, _, _]]
    }
    case None => Seq.empty
  }

  protected val primaryKeyComponents = partitionComponents ++ postPartitionComponents

  protected val actualizedComponents =
    indexedComponents ++ (primaryKeyComponents: Seq[RealizedPropComponent[_ >: P, _, _]])

  protected[cassandra] def indexedComponents: Set[RealizedPropComponent[_ >: P, _, _]] = {
    val keyComponents = realizedPType.keySet.filterNot(_.isInstanceOf[RealizedPrimaryKey[M, _, _]]).flatMap {
      _.realizedProp.realizedPropComponents: Seq[RealizedPropComponent[_ >: P, _, _]]
    }

    val indexComponents: Set[RealizedPropComponent[_ >: P, _, _]] = {
      val props = pType.indexSet.flatMap(_.props)
      val realizedProps = props.map(realizedPType.realizedProps(_))
      realizedProps.map(_.realizedPropComponents).flatten
    }

    keyComponents ++ indexComponents
  }

  protected val emblematicToJsonTranslator = new EmblematicToJsonTranslator {
    override protected val emblematic = modelType.emblematic
  }

  protected val jsonToEmblematicTranslator = new JsonToEmblematicTranslator {
    override protected val emblematic = modelType.emblematic
  }

  protected def columnName(prop: RealizedPropComponent[_, _, _]) = "prop_" + scoredPath(prop)

  protected def scoredPath(prop: RealizedPropComponent[_, _, _]) =
    prop.outerPropPath.inlinedPath.replace('.', '_')

  protected def jsonStringForP(p: P): String = {
    try {
      import org.json4s.native.JsonMethods._
      compact(render(emblematicToJsonTranslator.translate(p)(pTypeKey)))
    } catch {
      case e: CouldNotTraverseException =>
        throw new NotInDomainModelTranslationException(e.typeKey.name, e)
    }
  }

  protected def updateColumnNames(isCreate: Boolean = true): Seq[String] = {
    def names(components: Set[RealizedPropComponent[_ >: P, _, _]]) =
      components.map(columnName).toSeq.sorted
    val componentColumnNames = if (isCreate) names(actualizedComponents) else names(indexedComponents)
    val withP = "p" +: componentColumnNames
    val withDateTimes = if (persistenceConfig.writeTimestamps) {
      "created_timestamp" +: "updated_timestamp" +: withP
    } else {
      withP
    }
    val withRowVersion = if (persistenceConfig.optimisticLocking) {
      "row_version" +: withDateTimes
    } else {
      withDateTimes
    }
    val withId = if (isCreate && !hasPrimaryKey) {
      "id" +: withRowVersion
    } else {
      withRowVersion
    }
    withId
  }

  protected def updateColumnValues(state: PState[P], isCreate: Boolean = true): Seq[AnyRef] = {
    def values(components: Set[RealizedPropComponent[_ >: P, _, _]]) =
      components.toSeq.sortBy(columnName).map { component => propValBinding(component, state.get) }
    val componentColumnValues = if (isCreate) values(actualizedComponents) else values(indexedComponents)
    val withP = jsonStringForP(state.get) +: componentColumnValues
    val withDateTimes = if (persistenceConfig.writeTimestamps) {
      state.createdTimestamp.map(cassandraValue).orNull +:
      state.updatedTimestamp.map(cassandraValue).orNull +: withP
    } else {
      withP
    }
    val withRowVersion = if (persistenceConfig.optimisticLocking) {
      state.rowVersionOrNull +: withDateTimes
    } else {
      withDateTimes
    }
    val withId = if (isCreate && !hasPrimaryKey) {
      uuid(state) +: withRowVersion
    } else {
      withRowVersion
    }
    withId
  }

  protected def uuid(state: PState[P]) = state.id.get.asInstanceOf[CassandraId[P]].uuid

  protected def whereAssignments = if (hasPrimaryKey) {
    primaryKeyComponents.map(columnName).map(c => s"$c = :$c").mkString("\nAND\n  ")
  } else {
    "id = :id"
  }    

  protected def whereBindings(state: PState[P]) = if (hasPrimaryKey) {
    primaryKeyComponents.map(_.outerPropPath.get(state.get).asInstanceOf[AnyRef])
  } else {
    Seq(state.id.get.asInstanceOf[CassandraId[P]].uuid)
  }

  private def propValBinding[PP >: P, A](component: RealizedPropComponent[PP, _, A], p: P): AnyRef = {
    cassandraValue(component.outerPropPath.get(p))
  }

  protected def cassandraValue(value: Any): AnyRef = value match {
    case char: Char  => char.toString
    case d: DateTime => cassandraDate(d)
    case _           => value.asInstanceOf[AnyRef]
  }

  protected def cassandraDate(d: DateTime) = new java.util.Date(d.getMillis)

  protected def retrieveFromRow(row: Row): PState[P] = {
    val id = if (!hasPrimaryKey) {
      Some(CassandraId[P](row.getUUID("id")))
    } else {
      None
    }
    val rowVersion = if (persistenceConfig.optimisticLocking) {
      Option(row.getLong("row_version"))
    } else {
      None
    }
    val (createdTimestamp, updatedTimestamp) = if (persistenceConfig.writeTimestamps) {
      def toOptDateTime(c: String) = Option(row.getTimestamp(c)).map(new DateTime(_, DateTimeZone.UTC))
      (toOptDateTime("created_timestamp"), toOptDateTime("updated_timestamp"))
    } else {
      (None, None)
    }
    import org.json4s.native.JsonMethods._    
    val json = parse(row.getString("p"))
    val p = jsonToEmblematicTranslator.translate[P](json)(pTypeKey)
    PState[P](id, rowVersion, createdTimestamp, updatedTimestamp, p)
  }

  private var preparedStatements = Map[String, PreparedStatement]()

  protected def preparedStatement(cql: String): PreparedStatement = {
    synchronized {
      if (!preparedStatements.contains(cql)) {
        preparedStatements += cql -> session().prepare(cql)
      }
    }
    preparedStatements(cql)
  }

  override def toString = s"CassandraPRepo[${pTypeKey.name}]"

}

private[cassandra] object CassandraPRepo {

  def apply[F[_], M, P](
    effect: Effect[F],
    modelType: ModelType[M],
    pType: PType[M, P],
    config: PersistenceConfig,
    polyRepoOpt: Option[CassandraPRepo[F, M, _ >: P]],
    session: () => Session)
  : CassandraPRepo[F, M, P] = {
    val repo = pType match {
      case pt: PolyPType[_, _] =>
        new CassandraPRepo(effect, modelType, pType, config, session) with PolyCassandraPRepo[F, M, P]
      case pt: DerivedPType[_, _, _] =>
        def withPoly[Poly >: P](poly: CassandraPRepo[F, M, Poly]) = {
          class DerivedRepo extends {
            override protected val polyRepo: CassandraPRepo[F, M, Poly] = poly
          }
          with CassandraPRepo(effect, modelType, pType, config, session)
          with DerivedCassandraPRepo[F, M, P, Poly]
          new DerivedRepo
        }
        withPoly(polyRepoOpt.get)
      case _ =>
        new CassandraPRepo(effect, modelType, pType, config, session)
    }
    repo
  }

  private[cassandra] val basicToCassandraType = Map[TypeKey[_], String](
    typeKey[Boolean]  -> "boolean",
    typeKey[Char]     -> "text",
    typeKey[DateTime] -> "timestamp",
    typeKey[Double]   -> "double",
    typeKey[Float]    -> "float",
    typeKey[Int]      -> "int",
    typeKey[Long]     -> "bigint",
    typeKey[String]   -> "text")

}
