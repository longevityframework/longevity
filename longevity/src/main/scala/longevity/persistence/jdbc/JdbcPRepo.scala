package longevity.persistence.jdbc

import com.typesafe.scalalogging.LazyLogging
import java.sql.Connection
import java.sql.ResultSet
import java.util.UUID
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

private[persistence] class JdbcPRepo[F[_], M, P] private[persistence] (
  effect: Effect[F],
  modelType: ModelType[M],
  pType: PType[M, P],
  protected val persistenceConfig: PersistenceConfig,
  protected val connection: () => Connection)
extends PRepo[F, M, P](effect, modelType, pType)
with JdbcSchema[F, M, P]
with JdbcCreate[F, M, P]
with JdbcRetrieve[F, M, P]
with JdbcQuery[F, M, P]
with JdbcUpdate[F, M, P]
with JdbcDelete[F, M, P]
with LazyLogging {

  protected[jdbc] val tableName = camelToUnderscore(typeName(pTypeKey.tpe))

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

  protected[jdbc] def indexedComponents: Set[RealizedPropComponent[_ >: P, _, _]] = {
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
    if (isCreate && !hasPrimaryKey) {
      "id" +: withRowVersion
    } else {
      withRowVersion
    }
  }

  protected def updateColumnValues(state: PState[P], isCreate: Boolean = true): Seq[AnyRef] = {
    def values(components: Set[RealizedPropComponent[_ >: P, _, _]]) =
      components.toSeq.sortBy(columnName).map { component => propValBinding(component, state.get) }
    val componentColumnValues = if (isCreate) values(actualizedComponents) else values(indexedComponents)
    val withP = jsonStringForP(state.get) +: componentColumnValues
    val withDateTimes = if (persistenceConfig.writeTimestamps) {
      state.createdTimestamp.map(jdbcValue).orNull +: state.updatedTimestamp.map(jdbcValue).orNull +: withP
    } else {
      withP
    }
    val withRowVersion = if (persistenceConfig.optimisticLocking) {
      state.rowVersionOrNull +: withDateTimes
    } else {
      withDateTimes
    }
    if (isCreate && !hasPrimaryKey) {
      uuid(state) +: withRowVersion
    } else {
      withRowVersion
    }
  }

  protected def uuid(state: PState[P]) = state.id.get.asInstanceOf[JdbcId[P]].uuid

  protected def whereAssignments = if (hasPrimaryKey) {
    primaryKeyComponents.map(columnName).map(c => s"$c = :$c").mkString("\nAND\n  ")
  } else {
    "id = :id"
  }    

  protected def whereBindings(state: PState[P]) = if (hasPrimaryKey) {
    primaryKeyComponents.map(_.outerPropPath.get(state.get).asInstanceOf[AnyRef])
  } else {
    Seq(state.id.get.asInstanceOf[JdbcId[P]].uuid)
  }

  private def propValBinding[PP >: P, A](component: RealizedPropComponent[PP, _, A], p: P): AnyRef = {
    jdbcValue(component.outerPropPath.get(p))
  }

  protected def jdbcValue(value: Any): AnyRef = value match {
    case char: Char  => char.toString
    case d: DateTime => jdbcDate(d)
    case _           => value.asInstanceOf[AnyRef]
  }

  protected def jdbcDate(d: DateTime) = new java.util.Date(d.getMillis)

  // totally assumes you already called resultSet.next() and it returned true
  protected def retrieveFromResultSet(resultSet: ResultSet): PState[P] = {
    val id = if (!hasPrimaryKey) {
      Some(JdbcId[P](UUID.fromString(resultSet.getString("id"))))
    } else {
      None
    }
    val rowVersion = if (persistenceConfig.optimisticLocking) {
      Option(resultSet.getLong("row_version"))
    } else {
      None
    }
    val (createdTimestamp, updatedTimestamp) = if (persistenceConfig.writeTimestamps) {
      def toOptDateTime(c: String) = Option(resultSet.getDate(c)).map(new DateTime(_, DateTimeZone.UTC))
      (toOptDateTime("created_timestamp"), toOptDateTime("updated_timestamp"))
    } else {
      (None, None)
    }
    import org.json4s.native.JsonMethods._    
    val json = parse(resultSet.getString("p"))
    val p = jsonToEmblematicTranslator.translate[P](json)(pTypeKey)
    PState[P](id, rowVersion, createdTimestamp, updatedTimestamp, p)
  }

  /** converts a duplicate key exception (ie unique constraint violation) from the underlying database
   * driver into a [[longevity.exceptions.persistence.DuplicateKeyValException]], and throws the new
   * exception
   */
  protected def convertDuplicateKeyException(state: PState[P]): PartialFunction[Throwable, Unit] =
    PartialFunction.empty

  override def toString = s"JdbcPRepo[${pTypeKey.name}]"

}

private[persistence] object JdbcPRepo {

  def apply[F[_], M, P](
    effect: Effect[F],
    modelType: ModelType[M],
    pType: PType[M, P],
    config: PersistenceConfig,
    polyRepoOpt: Option[JdbcPRepo[F, M, _ >: P]],
    connection: () => Connection)
  : JdbcPRepo[F, M, P] = {
    val repo = pType match {
      case pt: PolyPType[_, _] =>
        new JdbcPRepo(effect, modelType, pType, config, connection) with PolyJdbcPRepo[F, M, P]
      case pt: DerivedPType[_, _, _] =>
        def withPoly[Poly >: P](poly: JdbcPRepo[F, M, Poly]) = {
          class DerivedRepo extends {
            override protected val polyRepo: JdbcPRepo[F, M, Poly] = poly
          }
          with JdbcPRepo(effect, modelType, pType, config, connection) with DerivedJdbcPRepo[F, M, P, Poly]
          new DerivedRepo
        }
        withPoly(polyRepoOpt.get)
      case _ =>
        new JdbcPRepo(effect, modelType, pType, config, connection)
    }
    repo
  }

  private[jdbc] val basicToJdbcType = Map[TypeKey[_], String](
    typeKey[Boolean]  -> "boolean",
    typeKey[Char]     -> "text",
    typeKey[DateTime] -> "timestamp",
    typeKey[Double]   -> "double",
    typeKey[Float]    -> "float",
    typeKey[Int]      -> "int",
    typeKey[Long]     -> "bigint",
    typeKey[String]   -> "text")

}
