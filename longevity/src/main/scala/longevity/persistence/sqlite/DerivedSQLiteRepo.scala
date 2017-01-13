package longevity.persistence.sqlite

import longevity.persistence.PState
import longevity.model.realized.RealizedPropComponent
import longevity.model.realized.RealizedKey
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.blocking

private[sqlite] trait DerivedSQLiteRepo[P, Poly >: P] extends SQLiteRepo[P] {

  protected val polyRepo: SQLiteRepo[Poly]

  override protected[sqlite] val tableName: String = polyRepo.tableName

  override protected def jsonStringForP(p: P): String = {
    // we use the poly type key here so we get the discriminator in the JSON
    import org.json4s.native.JsonMethods._
    compact(render(emblematicToJsonTranslator.translate[Poly](p)(polyRepo.pTypeKey)))
  }

  override protected[sqlite] def indexedComponents: Set[RealizedPropComponent[_ >: P, _, _]] = {
    myIndexedComponents ++ polyRepo.indexedComponents
  }

  private def myIndexedComponents = super.indexedComponents

  override protected[persistence] def createSchema()(implicit context: ExecutionContext)
  : Future[Unit] = Future {
    blocking {
      createActualizedPropColumns()
      createUniqueIndexes()
      createNonUniqueIndexes()
    }
  }

  private def createActualizedPropColumns(): Unit = {
    actualizedComponents.map {
      prop => addColumn(columnName(prop), componentToSQLiteType(prop))
    }
  }

  override protected def updateColumnNames(isCreate: Boolean = true): Seq[String] = {
    super.updateColumnNames(isCreate) :+ "discriminator"
  }

  override protected def updateColumnValues(state: PState[P], isCreate: Boolean = true): Seq[AnyRef] = {
    val discriminatorValue = state.get.getClass.getSimpleName
    super.updateColumnValues(state, isCreate) :+ discriminatorValue
  }

  override protected def keyValSelectStatementConjunction(key: RealizedKey[P, _]): String =
    super.keyValSelectStatementConjunction(key) + s"\nAND\n  discriminator = '$discriminatorValue'"

  override protected def queryWhereClause(filterInfo: FilterInfo): String =
    super.queryWhereClause(filterInfo) + s"\nAND\n  discriminator = '$discriminatorValue'"

  private def discriminatorValue = pTypeKey.name

}
