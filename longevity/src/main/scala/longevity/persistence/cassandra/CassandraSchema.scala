package longevity.persistence.cassandra

import com.datastax.driver.core.exceptions.InvalidQueryException
import longevity.model.realized.RealizedPropComponent
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.blocking

/** implementation of CassandraRepo.createSchema */
private[cassandra] trait CassandraSchema[M, P] {
  repo: CassandraRepo[M, P] =>

  protected[persistence] def createSchema()(implicit context: ExecutionContext): Future[Unit] = Future {
    blocking {
      logger.debug(s"creating schema for table $tableName")
      createTable()
      createIndexes()
      if (persistenceConfig.optimisticLocking) {
        addColumn("row_version", "bigint")
      }
      if (persistenceConfig.writeTimestamps) {
        addColumn("created_timestamp", "timestamp")
        addColumn("updated_timestamp", "timestamp")
      }
      logger.debug(s"done creating schema for table $tableName")
    }
  }

  protected def createTable(): Unit = {
    val createTable = s"""|
    |CREATE TABLE IF NOT EXISTS $tableName ($idDef
    |  p text,
    |  $actualizedComponentColumnDefs,
    |  $primaryKeyDef
    |)
    |WITH COMPRESSION = { 'sstable_compression': 'SnappyCompressor' };
    |""".stripMargin
    logger.debug(s"executing CQL: $createTable")
    session.execute(createTable)
  }

  private def idDef = if (hasPrimaryKey) "" else "\n  id uuid,"

  private def actualizedComponentColumnDefs = actualizedComponents.map(columnDef).mkString(",\n  ")

  private def primaryKeyDef = (hasPrimaryKey, postPartitionComponents.nonEmpty) match {
    case (true, true)  => s"PRIMARY KEY (($partitionColumns), $postPartitionColumns)"
    case (true, false) => s"PRIMARY KEY (($partitionColumns))"
    case _             => s"PRIMARY KEY (id)"
  }

  private def partitionColumns = partitionComponents.map(columnName).mkString(", ")

  private def postPartitionColumns = postPartitionComponents.map(columnName).mkString(", ")

  private def columnDef(component: RealizedPropComponent[_ >: P, _, _]) =
    s"${columnName(component)} ${componentToCassandraType(component)}"

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

  protected def componentToCassandraType[A](
    component: RealizedPropComponent[_ >: P, _, A])
  : String = {
    CassandraRepo.basicToCassandraType(component.componentTypeKey)
  }

  protected def createIndexes(): Unit = indexedComponents.foreach(createIndex)

  protected def createIndex(component: RealizedPropComponent[_ >: P, _, _]): Unit = {
    val indexName = s"${tableName}_${scoredPath(component)}"
    createIndex(indexName, columnName(component))
  }

  protected def createIndex(indexName: String, columnName: String): Unit = {
    val createIndex = s"CREATE INDEX IF NOT EXISTS $indexName ON $tableName ($columnName);"
    logger.debug(s"executing CQL: $createIndex")
    session.execute(createIndex)
  }

}
