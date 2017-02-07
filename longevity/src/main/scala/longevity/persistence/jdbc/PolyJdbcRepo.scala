package longevity.persistence.jdbc

import longevity.persistence.BasePolyRepo

private[persistence] trait PolyJdbcRepo[P] extends JdbcRepo[P] with BasePolyRepo[P] {

  override protected def createTable(): Unit = {
    super.createTable()
    addColumn("discriminator", "text")
    createIndex(false, s"${tableName}_discriminator", Seq("discriminator"))
  }

}
