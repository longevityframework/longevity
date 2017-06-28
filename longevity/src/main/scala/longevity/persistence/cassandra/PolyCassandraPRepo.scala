package longevity.persistence.cassandra

import longevity.persistence.BasePolyRepo

private[cassandra] trait PolyCassandraPRepo[F[_], M, P] extends CassandraPRepo[F, M, P] with BasePolyRepo[F, M, P] {

  override protected def createTable(): Unit = {
    super.createTable()
    addColumn("discriminator", "text")
    createIndex(s"${tableName}_discriminator", "discriminator")
  }

}
