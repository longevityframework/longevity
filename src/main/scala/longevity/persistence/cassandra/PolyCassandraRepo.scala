package longevity.persistence.cassandra

import longevity.subdomain.persistent.Persistent

private[cassandra] trait PolyCassandraRepo[P <: Persistent] extends CassandraRepo[P] {

  override protected def createTable(): Unit = {
    super.createTable()
    addColumn("discriminator", "text")
  }

}
