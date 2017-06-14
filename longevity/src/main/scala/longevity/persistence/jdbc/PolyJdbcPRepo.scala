package longevity.persistence.jdbc

import longevity.persistence.BasePolyRepo

private[persistence] trait PolyJdbcPRepo[M, P] extends JdbcPRepo[M, P] with BasePolyRepo[M, P] {

  override protected def createTable(): Unit = {
    super.createTable()
    addColumn("discriminator", "text")
    createIndex(false, s"${tableName}_discriminator", Seq("discriminator"))
  }

}
