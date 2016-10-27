package longevity.persistence.mongo

import com.mongodb.client.model.IndexOptions
import org.bson.BsonInt32
import org.bson.BsonDocument
import longevity.subdomain.Persistent
import longevity.subdomain.ptype.Index
import longevity.subdomain.realized.RealizedKey
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.blocking

/** implementation of MongoRepo.createSchema */
private[mongo] trait MongoSchema[P <: Persistent] {
  repo: MongoRepo[P] =>

  protected[persistence] def createSchema()(implicit context: ExecutionContext): Future[Unit] = Future {
    blocking {
      logger.debug(s"creating schema for collection $collectionName")

      realizedPType.keySet.map { key =>
        val paths = Seq(key.realizedProp.inlinedPath)
        val name = indexName(key)
        createIndex(paths, name, true)
      }

      pType.indexSet.map { index =>
        val paths = index.props.map(realizedPType.realizedProps(_).inlinedPath)
        val name = indexName(index)
        createIndex(paths, name, false)
      }

      logger.debug(s"done creating schema for collection $collectionName")
    }
  }

  protected def createIndex(paths: Seq[String], indexName: String, unique: Boolean): Unit = {
    val document = new BsonDocument
    paths.foreach { path => document.append(path, new BsonInt32(1)) }

    val options = new IndexOptions().name(indexName).unique(unique)

    logger.debug(s"calling MongoCollection.createIndex: $document $options")
    mongoCollection.createIndex(document, options)
  }

  protected def indexName(key: RealizedKey[P, _]): String =
    indexName(Seq(key.realizedProp.inlinedPath))

  private def indexName(index: Index[P]): String =
    indexName(index.props.map(realizedPType.realizedProps(_).inlinedPath))

  private def indexName(paths: Seq[String]): String = {
    val cappedSegments: Seq[String] = paths.map {
      path => path.split('.').mkString("_")
    }
    cappedSegments.mkString("__")
  }

}
