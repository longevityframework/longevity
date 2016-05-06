package longevity.persistence.cassandra

import emblem.TypeKey
import longevity.subdomain.persistent.Persistent
import longevity.subdomain.ptype.Prop
import java.util.UUID

private[cassandra] trait DerivedCassandraRepo[P <: Persistent, Poly >: P <: Persistent] extends CassandraRepo[P] {

  protected val polyRepo: CassandraRepo[Poly]

  override protected[cassandra] def tableName: String = polyRepo.tableName

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

  // Repo.create & Repo.update overrides:

  override protected def updateColumnNames(includeId: Boolean = true): Seq[String] = {
    super.updateColumnNames(includeId) :+ "discriminator"
  }

  override protected def updateColumnValues(uuid: UUID, p: P, includeId: Boolean = true): Seq[AnyRef] = {
    val discriminatorValue = p.getClass.getSimpleName
    super.updateColumnValues(uuid, p, includeId) :+ discriminatorValue
  }

  // Repo.retrieveByPersistedAssoc overrides:

  override protected def retrieveByPersistedAssocCql = {
    val discriminator = pTypeKey.name
    s"SELECT * FROM $tableName WHERE id = :id AND discriminator = '$discriminator'"
  }

  // Repo.retrieveByKeyVal overrides:

  // TODO

  // Repo.retrieveByQuery overrides:

  // TODO

  // Repo.delete overrides:

  // TODO

}
