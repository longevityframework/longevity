package longevity.persistence.cassandra

import emblem.TypeKey
import longevity.subdomain.persistent.Persistent
import longevity.subdomain.ptype.Key
import longevity.subdomain.ptype.Prop
import java.util.UUID

private[cassandra] trait DerivedCassandraRepo[P <: Persistent, Poly >: P <: Persistent] extends CassandraRepo[P] {

  protected val polyRepo: CassandraRepo[Poly]

  override protected[cassandra] val tableName: String = polyRepo.tableName

  override protected def jsonStringForP(p: P): String = {
    // we use the poly type key here so we get the discriminator in the JSON
    import org.json4s.native.JsonMethods._
    compact(render(persistentToJsonTranslator.traverse[Poly](p)(polyRepo.pTypeKey)))
  }

  private def myRealizedProps: List[Prop[_ >: P <: Persistent, _]] = super.realizedProps

  override protected[cassandra] def realizedProps: List[Prop[_ >: P <: Persistent, _]] = {
    myRealizedProps ++ polyRepo.realizedProps
  }

  override protected def createSchema(): Unit = {
    createRealizedPropColumns()
    createIndexes()
  }

  private def createRealizedPropColumns(): Unit = {
    realizedProps.map { prop =>
      addColumn(columnName(prop), typeKeyToCassandraType(prop.propTypeKey))
    }
  }

  override protected def updateColumnNames(includeId: Boolean = true): Seq[String] = {
    super.updateColumnNames(includeId) :+ "discriminator"
  }

  override protected def updateColumnValues(uuid: UUID, p: P, includeId: Boolean = true): Seq[AnyRef] = {
    val discriminatorValue = p.getClass.getSimpleName
    super.updateColumnValues(uuid, p, includeId) :+ discriminatorValue
  }

  override protected def retrieveByPersistedAssocCql: String =
    s"SELECT * FROM $tableName WHERE id = :id AND discriminator = '$discriminatorValue'"

  override protected def keyValSelectStatementConjunction(key: Key[P]): String =
    super.keyValSelectStatementConjunction(key) + s"\nAND\n  discriminator = '$discriminatorValue'"

  override protected def retrieveByQueryConjunction(queryInfo: QueryInfo): String =
    super.retrieveByQueryConjunction(queryInfo) + s"\nAND\n  discriminator = '$discriminatorValue'"

  private def discriminatorValue = pTypeKey.name

}
