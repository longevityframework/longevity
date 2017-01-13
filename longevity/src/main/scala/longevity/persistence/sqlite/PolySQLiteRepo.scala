package longevity.persistence.sqlite

import longevity.persistence.BasePolyRepo

private[sqlite] trait PolySQLiteRepo[P] extends SQLiteRepo[P] with BasePolyRepo[P] {

  override protected def createTable(): Unit = {
    super.createTable()
    addColumn("discriminator", "text")
    createIndex(false, s"${tableName}_discriminator", Seq("discriminator"))
  }

}
