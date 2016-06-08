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
    pType.keySet.foreach { key =>
      val paths = key.props.map(_.path)
      createIndex(paths, keyName(key), true)
    }

    val keyProps = pType.keySet.map(_.props)
    pType.indexSet.foreach { index =>
      if (!keyProps.contains(index.props)) {
        val paths = index.props.map(_.path)
        createIndex(paths, indexName(index), false)
      }
    }
  }

  protected def createIndex(paths: Seq[String], indexName: String, unique: Boolean): Unit = {
    val mongoPaths = paths map (_ -> 1)
    mongoCollection.createIndex(MongoDBObject(mongoPaths.toList), indexName, unique)
  }

  protected def keyName(key: Key[P]): String = {
    val paths = key.props.map(_.path)
    indexName(paths, true)
  }

  private def indexName(index: Index[P]): String = {
    val paths = index.props.map(_.path)
    indexName(paths, false)
  }

  private def indexName(paths: Seq[String], unique: Boolean): String = {
    val cappedSegments: Seq[String] = paths.map {
      path => path.split('.').mkString("_")
    }
    val prefix = if (unique) "key" else "index"
    s"""${prefix}__${cappedSegments.mkString("__")}"""
  }

}
