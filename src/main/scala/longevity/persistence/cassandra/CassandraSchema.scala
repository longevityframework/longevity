package longevity.persistence.cassandra

import com.datastax.driver.core.exceptions.InvalidQueryException
import longevity.subdomain.persistent.Persistent
import longevity.subdomain.realized.RealizedPropComponent

/** implementation of CassandraRepo.createSchema */
private[cassandra] trait CassandraSchema[P <: Persistent] {
  repo: CassandraRepo[P] =>

  protected def createSchema(): Unit = {
    logger.debug(s"creating schema for table $tableName")
    createTable()
    createIndexes()
    if (persistenceConfig.optimisticLocking) {
      addColumn("row_version", "bigint")
    }
    logger.debug(s"done creating schema for table $tableName")
  }

  protected def createTable(): Unit = {
    def columnDefs(component: RealizedPropComponent[_ >: P <: Persistent, _, _]) = {
      s"${columnName(component)} ${componentToCassandraType(component)}"
    }
    val actualizedComponentColumnDefs = actualizedComponents.map(columnDefs).mkString(",\n  ")
    val createTable = s"""|
    |CREATE TABLE IF NOT EXISTS $tableName (
    |  id uuid,
    |  p text,
    |  $actualizedComponentColumnDefs,
    |  PRIMARY KEY (id)
    |)
    |WITH COMPRESSION = { 'sstable_compression': 'SnappyCompressor' };
    |""".stripMargin
    logger.debug(s"executing CQL: $createTable")
    session.execute(createTable)
  }

  protected def addColumn(columnName: String, columnType: String): Unit = {
    val cql = s"ALTER TABLE $tableName ADD $columnName $columnType"
    logger.debug(s"executing CQL: $cql")
    try {
      session.execute(cql)
    } catch {
      case e: InvalidQueryException
        if e.getMessage.contains("because it conflicts with an existing column") =>
        // ignoring this exception is recommended ALTER TABLE ADD IF NOT EXISTS
        // http://stackoverflow.com/questions/25728944/cassandra-add-column-if-not-exists
    }
  }

  protected def componentToCassandraType[A](component: RealizedPropComponent[_ >: P <: Persistent, _, A])
  : String = {
    CassandraRepo.basicToCassandraType(component.componentTypeKey)
  }

  protected def createIndexes(): Unit = actualizedComponents.foreach(createIndex)

  protected def createIndex(component: RealizedPropComponent[_ >: P <: Persistent, _, _]): Unit = {
    val indexName = s"${tableName}_${scoredPath(component)}"
    createIndex(indexName, columnName(component))
  }

  protected def createIndex(indexName: String, columnName: String): Unit = {
    val createIndex = s"CREATE INDEX IF NOT EXISTS $indexName ON $tableName ($columnName);"
    logger.debug(s"executing CQL: $createIndex")
    session.execute(createIndex)
  }

}
