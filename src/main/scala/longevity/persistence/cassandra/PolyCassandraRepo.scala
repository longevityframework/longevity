package longevity.persistence.cassandra

import longevity.persistence.BasePolyRepo
import longevity.persistence.PState
import longevity.subdomain.persistent.Persistent

private[cassandra] trait PolyCassandraRepo[P <: Persistent] extends CassandraRepo[P] with BasePolyRepo[P] {

  override protected def createTable(): Unit = {
    super.createTable()
    addColumn("discriminator", "text")
    createIndex(s"${tableName}_discriminator", "discriminator")
  }

}
