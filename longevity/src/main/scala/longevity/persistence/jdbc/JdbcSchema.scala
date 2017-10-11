package longevity.persistence.jdbc

import longevity.model.realized.RealizedPrimaryKey
import longevity.model.realized.RealizedProp
import longevity.model.realized.RealizedPropComponent

/** implementation of JdbcPRepo.createSchema */
private[jdbc] trait JdbcSchema[F[_], M, P] {
  repo: JdbcPRepo[F, M, P] =>

  protected[persistence] def createSchemaBlocking(): Unit = {
    logger.debug(s"creating schema for table $tableName")
    createTable()
    createUniqueIndexes()
    createNonUniqueIndexes()
    if (persistenceConfig.optimisticLocking) {
      addColumn("row_version", "bigint")
    }
    if (persistenceConfig.writeTimestamps) {
      addColumn("created_timestamp", "timestamp")
      addColumn("updated_timestamp", "timestamp")
    }
    logger.debug(s"done creating schema for table $tableName")
  }

  protected def createTable(): Unit = {
    val createTable = s"""|
    |CREATE TABLE IF NOT EXISTS $tableName ($idDef
    |  p text,
    |  $actualizedComponentColumnDefs
    |  $primaryKeyDef
    |)
    |""".stripMargin
    logger.debug(s"executing SQL: $createTable")
    connection().prepareStatement(createTable).execute()
  }

  private def idDef = if (hasPrimaryKey) "" else "\n  id text,"

  private def actualizedComponentColumnDefs = {
    val s = actualizedComponents.map(columnDef).mkString(",\n  ")
    if (s.isEmpty) s else s"$s,"
  }

  private def primaryKeyDef = if (hasPrimaryKey) s"PRIMARY KEY ($primaryKeyColumns)" else s"PRIMARY KEY (id)"

  private def primaryKeyColumns = primaryKeyComponents.map(columnName).mkString(", ")

  private def columnDef(component: RealizedPropComponent[_ >: P, _, _]) =
    s"${columnName(component)} ${componentToJdbcType(component)}"

  protected def addColumn(columnName: String, columnType: String): Unit = {
    val sql = s"ALTER TABLE $tableName ADD COLUMN IF NOT EXISTS $columnName $columnType"
    logger.debug(s"executing SQL: $sql")
    connection().prepareStatement(sql).execute()
  }

  protected def componentToJdbcType[A](
    component: RealizedPropComponent[_ >: P, _, A])
  : String = {
    JdbcPRepo.basicToJdbcType(component.componentTypeKey)
  }

  protected def createUniqueIndexes(): Unit = {
    val nonPrimaryKeys = realizedPType.keySet.filterNot(_.isInstanceOf[RealizedPrimaryKey[M, _, _]])
    nonPrimaryKeys.foreach { key =>
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
    connection().prepareStatement(createIndex).execute()
  }

  private def scoredPath(prop: RealizedProp[_, _]) = prop.inlinedPath.replace('.', '_')

  protected[persistence] def createMigrationSchemaBlocking(): Unit = {
    addColumn("migration_started", "boolean")
    addColumn("migration_complete", "boolean")
    createIndex(false, s"${tableName}_migration_complete", Seq("migration_complete"))
  }

  protected[persistence] def dropSchemaBlocking(): Unit = {
    val dropTable = s"DROP TABLE IF EXISTS $tableName"
    logger.debug(s"executing SQL: $dropTable")
    connection().prepareStatement(dropTable).execute()
  }

}
