package longevity.persistence.sqlite

import longevity.model.realized.RealizedPartitionKey
import longevity.model.realized.RealizedProp
import longevity.model.realized.RealizedPropComponent
import org.sqlite.SQLiteException
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.blocking

/** implementation of SQLiteRepo.createSchema */
private[sqlite] trait SQLiteSchema[P] {
  repo: SQLiteRepo[P] =>

  protected[persistence] def createSchema()(implicit context: ExecutionContext): Future[Unit] = Future {
    blocking {
      logger.debug(s"creating schema for table $tableName")
      createTable()
      createUniqueIndexes()
      createNonUniqueIndexes()
      if (persistenceConfig.optimisticLocking) {
        addColumn("row_version", "bigint")
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
    |""".stripMargin
    logger.debug(s"executing SQL: $createTable")
    connection.prepareStatement(createTable).execute()
  }

  private def idDef = if (hasPartitionKey) "" else "\n  id text,"

  private def actualizedComponentColumnDefs = actualizedComponents.map(columnDef).mkString(",\n  ")

  private def primaryKeyDef = if (hasPartitionKey) s"PRIMARY KEY ($partitionColumns)" else s"PRIMARY KEY (id)"

  private def partitionColumns = partitionComponents.map(columnName).mkString(", ")

  private def columnDef(component: RealizedPropComponent[_ >: P, _, _]) =
    s"${columnName(component)} ${componentToSQLiteType(component)}"

  protected def addColumn(columnName: String, columnType: String): Unit = {
    val sql = s"ALTER TABLE $tableName ADD $columnName $columnType"
    logger.debug(s"executing SQL: $sql")
    try {
      connection.prepareStatement(sql).execute()
    } catch {
      // ignoring this exception is best approximation of ALTER TABLE ADD IF NOT EXISTS
      case e: SQLiteException if e.getMessage.contains("duplicate column name: row_version") =>
    }
  }

  protected def componentToSQLiteType[A](
    component: RealizedPropComponent[_ >: P, _, A])
  : String = {
    SQLiteRepo.basicToSQLiteType(component.componentTypeKey)
  }

  protected def createUniqueIndexes(): Unit = {
    val nonPartitionKeys = realizedPType.keySet.filterNot(_.isInstanceOf[RealizedPartitionKey[_, _]])
    nonPartitionKeys.foreach { key =>
      val indexName = s"${tableName}__${scoredPath(key.realizedProp)}"
      val columnsNames = key.realizedProp.realizedPropComponents.map(columnName)
      createIndex(true, indexName, columnsNames)
    }
  }

  protected def createNonUniqueIndexes(): Unit = {
    pType.indexSet.foreach { index =>
      val realizedProps = index.props.map(realizedPType.realizedProps(_))
      val scoredPaths = realizedProps.map(scoredPath)
      val indexName = s"""${tableName}__${scoredPaths.mkString("__")}"""
      def toComponents(prop: RealizedProp[_, _]): Seq[RealizedPropComponent[_, _, _]] =
        prop.realizedPropComponents
      val columnsNames = realizedProps.flatMap(toComponents).map(columnName)
      createIndex(false, indexName, columnsNames)
    }
  }

  protected def createIndex(unique: Boolean, indexName: String, columnNames: Seq[String]): Unit = {
    val prefix = if (unique) "CREATE UNIQUE INDEX" else "CREATE INDEX"
    val columnList = columnNames.mkString(", ")
    val createIndex = s"$prefix IF NOT EXISTS $indexName ON $tableName ($columnList);"
    logger.debug(s"executing SQL: $createIndex")
    connection.prepareStatement(createIndex).execute()
  }

}
