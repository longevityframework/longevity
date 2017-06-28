package longevity.persistence.cassandra

import longevity.persistence.PState
import longevity.model.realized.RealizedPropComponent
import longevity.model.realized.RealizedKey

private[cassandra] trait DerivedCassandraPRepo[F[_], M, P, Poly >: P] extends CassandraPRepo[F, M, P] {

  protected val polyRepo: CassandraPRepo[F, M, Poly]

  override protected[cassandra] val tableName: String = polyRepo.tableName

  override protected def jsonStringForP(p: P): String = {
    // we use the poly type key here so we get the discriminator in the JSON
    import org.json4s.native.JsonMethods._
    compact(render(emblematicToJsonTranslator.translate[Poly](p)(polyRepo.pTypeKey)))
  }

  override protected[cassandra] def indexedComponents: Set[RealizedPropComponent[_ >: P, _, _]] = {
    myIndexedComponents ++ polyRepo.indexedComponents
  }

  private def myIndexedComponents = super.indexedComponents

  override protected[persistence] def createSchemaBlocking(): Unit = {
    createActualizedPropColumns()
    createIndexes()
  }

  private def createActualizedPropColumns(): Unit = {
    actualizedComponents.map {
      prop => addColumn(columnName(prop), componentToCassandraType(prop))
    }
  }

  override protected def updateColumnNames(isCreate: Boolean = true): Seq[String] = {
    super.updateColumnNames(isCreate) :+ "discriminator"
  }

  override protected def updateColumnValues(state: PState[P], isCreate: Boolean = true): Seq[AnyRef] = {
    val discriminatorValue = state.get.getClass.getSimpleName
    super.updateColumnValues(state, isCreate) :+ discriminatorValue
  }

  override protected def keyValSelectStatementConjunction(key: RealizedKey[M, P, _]): String =
    super.keyValSelectStatementConjunction(key) + s"\nAND\n  discriminator = '$discriminatorValue'"

  override protected def queryWhereClause(filterInfo: FilterInfo): String =
    super.queryWhereClause(filterInfo) + s"\nAND\n  discriminator = '$discriminatorValue'"

  override protected[cassandra] def deleteStatement = polyRepo.deleteStatement

  private def discriminatorValue = pTypeKey.name

}
