package longevity.persistence.cassandra

import emblem.TypeKey
import emblem.typeKey
import longevity.subdomain.Assoc
import longevity.subdomain.persistent.Persistent
import longevity.subdomain.ptype.Prop

/** implementation of CassandraRepo.createSchema */
private[cassandra] trait CassandraSchema[P <: Persistent] {
  repo: CassandraRepo[P] =>

  protected def createSchema(): Unit = {
    createTable()
    createIndexes()
  }

  private def createTable(): Unit = {
    val realizedPropColumns = realizedProps.map(prop =>
      s"  ${columnName(prop)} ${typeKeyToCassandraType(prop.propTypeKey)},"
    ).mkString("\n")
    val createTable = s"""|
    |CREATE TABLE IF NOT EXISTS $tableName (
    |  id uuid,
    |  p text,
    |$realizedPropColumns
    |  PRIMARY KEY (id)
    |)
    |WITH COMPRESSION = { 'sstable_compression': 'SnappyCompressor' };
    |""".stripMargin
    session.execute(createTable)
  }

  private def typeKeyToCassandraType[A](key: TypeKey[A]): String = {
    if (key <:< typeKey[Assoc[_ <: Persistent]]) {
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

  private def createIndex(prop: Prop[P, _]): Unit = {
    val name = s"""${tableName}_${scoredPath(prop)}"""
    val createIndex = s"CREATE INDEX IF NOT EXISTS $name ON $tableName (${columnName(prop)});"
    session.execute(createIndex)
  }

}
