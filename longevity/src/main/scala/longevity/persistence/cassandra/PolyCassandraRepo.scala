package longevity.persistence.cassandra

import longevity.persistence.BasePolyRepo

private[cassandra] trait PolyCassandraRepo[M, P] extends CassandraRepo[M, P] with BasePolyRepo[M, P] {

  override protected def createTable(): Unit = {
    super.createTable()
    addColumn("discriminator", "text")
    createIndex(s"${tableName}_discriminator", "discriminator")
  }

}
