package longevity.persistence.cassandra

import emblem.imports._
import longevity.subdomain._
import longevity.subdomain.root.Prop

private[cassandra] trait CassandraSchema[R <: Root] {
  repo: CassandraRepo[R] =>

  protected def createSchema(): Unit = {
    createTable()
    createIndexes()
  }

  private def createTable(): Unit = {
    val realizedPropColumns = realizedProps.map(prop =>
      s"  ${columnName(prop)} ${typeKeyToCassandraType(prop.typeKey)},"
    ).mkString("\n")
    val createTable = s"""|
    |CREATE TABLE IF NOT EXISTS $tableName (
    |  id uuid,
    |  root text,
    |$realizedPropColumns
    |  PRIMARY KEY (id)
    |)
    |WITH COMPRESSION = { 'sstable_compression': 'SnappyCompressor' };
    |""".stripMargin
    session.execute(createTable)
  }

  private def typeKeyToCassandraType[A](key: TypeKey[A]): String = {
    if (key <:< typeKey[Assoc[_ <: Root]]) {
      "uuid"
    } else if (CassandraRepo.basicToCassandraType.contains(key)) {
      CassandraRepo.basicToCassandraType(key)
    } else if (shorthandPool.contains(key)) {
      CassandraRepo.basicToCassandraType(shorthandPool(key).abbreviatedTypeKey)
    } else {
      throw new RuntimeException(s"unexpected prop type ${key.tpe}")
    }
  }

  private def createIndexes(): Unit = realizedProps.foreach(createIndex)

  private def createIndex(prop: Prop[R, _]): Unit = {
    val name = s"""${tableName}_${scoredPath(prop)}"""
    val createIndex = s"CREATE INDEX IF NOT EXISTS $name ON $tableName (${columnName(prop)});"
    session.execute(createIndex)
  }

}
