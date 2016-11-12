package longevity.persistence.cassandra

import longevity.persistence.BasePolyRepo

private[cassandra] trait PolyCassandraRepo[P] extends CassandraRepo[P] with BasePolyRepo[P] {

  override protected def createTable(): Unit = {
    super.createTable()
    addColumn("discriminator", "text")
    createIndex(s"${tableName}_discriminator", "discriminator")
  }

}
