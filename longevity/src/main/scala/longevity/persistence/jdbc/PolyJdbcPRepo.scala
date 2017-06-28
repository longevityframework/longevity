package longevity.persistence.jdbc

import longevity.persistence.BasePolyRepo

private[persistence] trait PolyJdbcPRepo[F[_], M, P] extends JdbcPRepo[F, M, P] with BasePolyRepo[F, M, P] {

  override protected def createTable(): Unit = {
    super.createTable()
    addColumn("discriminator", "text")
    createIndex(false, s"${tableName}_discriminator", Seq("discriminator"))
  }

}
