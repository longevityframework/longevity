package longevity.persistence.cassandra

import emblem.TypeKey
import longevity.subdomain.persistent.Persistent
import longevity.subdomain.ptype.Prop
import java.util.UUID

private[cassandra] trait DerivedCassandraRepo[P <: Persistent, Poly >: P <: Persistent] extends CassandraRepo[P] {

  protected val polyRepo: CassandraRepo[Poly]

  override protected[cassandra] def tableName: String = {
    polyRepo.tableName
  }

  override protected def jsonStringForP(p: P): String = {
    import org.json4s.native.JsonMethods._
    compact(render(persistentToJsonTranslator.traverse[Poly](p)(polyRepo.pTypeKey)))
  }

  private def myRealizedProps: List[Prop[_ >: P <: Persistent, _]] = super.realizedProps

  override protected[cassandra] def realizedProps: List[Prop[_ >: P <: Persistent, _]] = {
    myRealizedProps ++ polyRepo.realizedProps
  }

  // Repo.createSchema overrides:

  override protected def createSchema(): Unit = {
    createRealizedPropColumns()
    createIndexes()
  }

  private def createRealizedPropColumns(): Unit = {
    realizedProps.map { prop =>
      addColumn(columnName(prop), typeKeyToCassandraType(prop.propTypeKey))
    }
  }

  // Repo.create overrides:

  override protected def insertColumnNames: Seq[String] = {
    super.insertColumnNames :+ "discriminator"
  }

  override protected def insertColumnValues(uuid: UUID, p: P): Seq[AnyRef] = {
    val discriminatorValue = p.getClass.getSimpleName
    super.insertColumnValues(uuid, p) :+ discriminatorValue
  }

  // Repo.retrieveByPersistedAssoc overrides:

  // TODO

  // Repo.retrieveByKeyVal overrides:

  // TODO

  // Repo.retrieveByQuery overrides:

  // TODO

  // Repo.update overrides:

  // TODO

  // Repo.delete overrides:

  // TODO

}
