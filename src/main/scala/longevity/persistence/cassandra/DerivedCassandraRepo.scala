package longevity.persistence.cassandra

import longevity.subdomain.persistent.Persistent

private[cassandra] trait DerivedCassandraRepo[P <: Persistent] extends CassandraRepo[P] {

  val polyRepo: CassandraRepo[_ >: P <: Persistent]

  override protected[cassandra] def tableName = {
    polyRepo.tableName
  }

  override protected def createSchema(): Unit = {
    createRealizedPropColumns()
    createIndexes()
  }

}
