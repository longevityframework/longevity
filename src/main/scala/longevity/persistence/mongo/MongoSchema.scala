package longevity.persistence.mongo

import com.mongodb.casbah.commons.MongoDBObject
import longevity.subdomain.persistent.Persistent
import longevity.subdomain.ptype.Index
import longevity.subdomain.realized.RealizedKey

// this will find a better home in pt #106611128

/** implementation of MongoRepo.createSchema */
private[mongo] trait MongoSchema[P <: Persistent] {
  repo: MongoRepo[P] =>

  protected def createSchema(): Unit = {
    logger.debug(s"creating schema for collection $collectionName")

    var indexNames = Set[String]()

    realizedPType.keySet.foreach { key =>
      val paths = Seq(key.realizedProp.inlinedPath)
      val name = indexName(key)
      if (!indexNames.contains(name)) {
        indexNames += name
        createIndex(paths, name, true)
      }
    }

    pType.indexSet.foreach { index =>
      val paths = index.props.map(realizedPType.realizedProps(_).inlinedPath)
      val name = indexName(index)
      if (!indexNames.contains(name)) {
        indexNames += name
        createIndex(paths, name, false)
      }
    }

    logger.debug(s"done creating schema for collection $collectionName")
  }

  protected def createIndex(paths: Seq[String], indexName: String, unique: Boolean): Unit = {
    val mongoPaths = paths map (_ -> 1)
    val bson = MongoDBObject(mongoPaths.toList)
    logger.debug(s"calling MongoCollection.createIndex: $bson $indexName $unique")
    mongoCollection.createIndex(bson, indexName, unique)
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
