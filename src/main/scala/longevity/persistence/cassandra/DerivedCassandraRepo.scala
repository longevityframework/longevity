package longevity.persistence.cassandra

import longevity.subdomain.persistent.Persistent
import longevity.subdomain.realized.RealizedPropComponent
import longevity.subdomain.realized.RealizedKey
import java.util.UUID

private[cassandra] trait DerivedCassandraRepo[P <: Persistent, Poly >: P <: Persistent] extends CassandraRepo[P] {

  protected val polyRepo: CassandraRepo[Poly]

  override protected[cassandra] val tableName: String = polyRepo.tableName

  override protected def jsonStringForP(p: P): String = {
    // we use the poly type key here so we get the discriminator in the JSON
    import org.json4s.native.JsonMethods._
    compact(render(emblematicToJsonTranslator.translate[Poly](p)(polyRepo.pTypeKey)))
  }

  private def myActualizedComponents: List[RealizedPropComponent[_ >: P <: Persistent, _, _]] =
    super.actualizedComponents

  override protected[cassandra] def actualizedComponents
  : List[RealizedPropComponent[_ >: P <: Persistent, _, _]] = {
    myActualizedComponents ++ polyRepo.actualizedComponents
  }

  override protected def createSchema(): Unit = {
    createActualizedPropColumns()
    createIndexes()
  }

  private def createActualizedPropColumns(): Unit = {
    actualizedComponents.map {
      prop => addColumn(columnName(prop), componentToCassandraType(prop))
    }
  }

  override protected def updateColumnNames(includeId: Boolean = true): Seq[String] = {
    super.updateColumnNames(includeId) :+ "discriminator"
  }

  override protected def updateColumnValues(uuid: UUID, p: P, includeId: Boolean = true): Seq[AnyRef] = {
    val discriminatorValue = p.getClass.getSimpleName
    super.updateColumnValues(uuid, p, includeId) :+ discriminatorValue
  }

  override protected def keyValSelectStatementConjunction(key: RealizedKey[P, _]): String =
    super.keyValSelectStatementConjunction(key) + s"\nAND\n  discriminator = '$discriminatorValue'"

  override protected def retrieveByQueryConjunction(queryInfo: QueryInfo): String =
    super.retrieveByQueryConjunction(queryInfo) + s"\nAND\n  discriminator = '$discriminatorValue'"

  private def discriminatorValue = pTypeKey.name

}
