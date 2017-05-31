package longevity.persistence.mongo

import com.mongodb.MongoCommandException
import com.mongodb.client.model.IndexOptions
import org.bson.BsonInt32
import org.bson.BsonString
import org.bson.BsonBoolean
import org.bson.BsonDocument
import longevity.model.ptype.Index
import longevity.model.ptype.Partition
import longevity.model.realized.RealizedKey
import longevity.model.realized.RealizedPrimaryKey
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.blocking

/** implementation of MongoRepo.createSchema */
private[mongo] trait MongoSchema[M, P] {
  repo: MongoRepo[M, P] =>

  protected[persistence] def createSchema()(implicit context: ExecutionContext): Future[Unit] = Future {
    blocking {
      logger.debug(s"creating schema for collection $collectionName")
      new SchemaCreator().createSchema()
      logger.debug(s"done creating schema for collection $collectionName")
    }
  }

  private class SchemaCreator {

    def createSchema(): Unit = {
      realizedPType.keySet.foreach(createKey)
      pType.indexSet.foreach(createIndex)
      realizedPType.primaryKey.foreach(createPrimaryKey)
    }

    private def createKey(key: RealizedKey[M, P, _]): Unit = {
      val paths = key match {
        case p: RealizedPrimaryKey[M, P, _] if !p.fullyPartitioned => p.props.map(_.inlinedPath)
        case _ => Seq(key.realizedProp.inlinedPath)
      }

      val name = indexName(key)
      val hashed = key match {
        case p: RealizedPrimaryKey[M, P, _] if p.key.hashed => true
        case _ => false
      }

      // if there is a primary key, no other keys can be unique
      def unique = realizedPType.primaryKey.map(pk => pk == key && !pk.hashed).getOrElse(true)

      MongoSchema.this.createIndex(paths, name, unique, hashed)
    }

    private def createIndex(index: Index[P]): Unit = {
      val paths = index.props.map(realizedPType.realizedProps(_).inlinedPath)
      val name = indexName(index)
      MongoSchema.this.createIndex(paths, name, false)
    }

  }

  private def createPrimaryKey(key: RealizedPrimaryKey[M, P, _]): Unit = {
    val shardPaths = key.partition.props.map(realizedPType.realizedProps(_).inlinedPath)
    val shardType = if (key.hashed) new BsonString("hashed") else new BsonInt32(1)
    val shardKey = new BsonDocument
    shardPaths.foreach { shardPath => shardKey.append(shardPath, shardType) }

    val dbName = session.config.db
    val adminDb = session.client.getDatabase("admin")

    try {
      adminDb.runCommand(new BsonDocument("enableSharding", new BsonString(dbName)))
    } catch {
      case e: MongoCommandException if e.getCode == 59 =>
        logger.info(
          s"could not run enableSharding command on admin database. this is likely because you are using " +
          s"primary keys on an unsharded database. this is perfectly fine, nothing to worry about. " +
          s"here's the nested message: ${e.getMessage}")
      case e: MongoCommandException if e.getCode == 23 =>
        logger.info(
          s"could not run enableSharding command on admin database. this is likely because sharding has " +
          s"already been enabled, e.g., by a previous run of Repo.createSchema. this is perfectly fine, " +
          s"nothing to worry about. " +
          s"here's the nested message: ${e.getMessage}")
    }

    try {
      val shardCommand = new BsonDocument
      shardCommand.append("shardCollection", new BsonString(s"$dbName.$collectionName"))
      shardCommand.append("key", shardKey)
      shardCommand.append("unique", new BsonBoolean(key.fullyPartitioned && !key.hashed))

      adminDb.runCommand(shardCommand)
    } catch {
      case e: MongoCommandException if e.getCode == 59 =>
        logger.info(
          s"could not run shardCollection command on admin database. this is likely because you are using " +
          s"primary keys on an unsharded database. this is perfectly fine, nothing to worry about. " +
          s"here's the nested message: ${e.getMessage}")
      case e: MongoCommandException if e.getCode == 20 =>
        logger.info(
          s"could not run shardCollection command on admin database. this is likely because the collection " +
          s"has already been sharded, e.g., by a previous run of Repo.createSchema. assuming that the " +
          s"sharding matches your primary key this is perfectly fine, nothing to worry about. " +
          s"here's the nested message: ${e.getMessage}")
    }
  }

  protected def indexName(key: RealizedKey[M, P, _]): String =
    indexName(Seq(key.realizedProp.inlinedPath))

  private def indexName(index: Index[P]): String =
    indexName(index.props.map(realizedPType.realizedProps(_).inlinedPath))

  private def indexName(partition: Partition[P]): String =
    indexName(partition.props.map(realizedPType.realizedProps(_).inlinedPath))

  private def indexName(paths: Seq[String]): String = {
    val cappedSegments: Seq[String] = paths.map {
      path => path.split('.').mkString("_")
    }
    cappedSegments.mkString("__")
  }

  protected def createIndex(paths: Seq[String], indexName: String, unique: Boolean): Unit = {
    val document = new BsonDocument
    paths.foreach { path => document.append(path, new BsonInt32(1)) }

    val options = new IndexOptions().name(indexName).unique(unique)

    logger.debug(s"calling MongoCollection.createIndex: $document $options")
    mongoCollection.createIndex(document, options)
  }

  protected def createIndex(
    paths: Seq[String],
    indexName: String,
    unique: Boolean,
    hashed: Boolean = false)
  : Unit = {
    val shardType = if (hashed) new BsonString("hashed") else new BsonInt32(1)
    val document = new BsonDocument
    paths.foreach { path => document.append(path, shardType) }

    val options = new IndexOptions().name(indexName).unique(unique)

    logger.debug(s"calling MongoCollection.createIndex: $document $options")
    mongoCollection.createIndex(document, options)
  }

}
