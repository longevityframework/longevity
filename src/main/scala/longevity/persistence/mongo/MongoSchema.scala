package longevity.persistence.mongo

import com.mongodb.casbah.commons.MongoDBObject
import longevity.subdomain.persistent.Persistent
import longevity.subdomain.ptype.Index
import longevity.subdomain.ptype.Key

// this will find a better home in pt #106611128

/** implementation of MongoRepo.createSchema */
private[mongo] trait MongoSchema[P <: Persistent] {
  repo: MongoRepo[P] =>

  protected def createSchema(): Unit = {
    var indexNames = Set[String]()

    pType.keySet.foreach { key =>
      val paths = key.props.map(_.inlinedPath)
      val name = indexName(key)
      if (!indexNames.contains(name)) {
        indexNames += name
        createIndex(paths, name, true)
      }
    }

    pType.indexSet.foreach { index =>
      val paths = index.props.map(_.inlinedPath)
      val name = indexName(index)
      if (!indexNames.contains(name)) {
        indexNames += name
        createIndex(paths, name, false)
      }
    }
  }

  protected def createIndex(paths: Seq[String], indexName: String, unique: Boolean): Unit = {
    val mongoPaths = paths map (_ -> 1)
    mongoCollection.createIndex(MongoDBObject(mongoPaths.toList), indexName, unique)
  }

  protected def indexName(key: Key[P]): String =
    indexName(key.props.map(_.inlinedPath))

  private def indexName(index: Index[P]): String =
    indexName(index.props.map(_.inlinedPath))

  private def indexName(paths: Seq[String]): String = {
    val cappedSegments: Seq[String] = paths.map {
      path => path.split('.').mkString("_")
    }
    cappedSegments.mkString("__")
  }

}
