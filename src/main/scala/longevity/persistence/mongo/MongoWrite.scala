package longevity.persistence.mongo

import com.mongodb.DBObject
import com.mongodb.DuplicateKeyException
import com.mongodb.casbah.commons.Implicits.wrapDBObj
import com.mongodb.casbah.commons.MongoDBObject
import longevity.exceptions.persistence.DuplicateKeyValException
import longevity.subdomain.persistent.Persistent

/** utilities for writing to a mongo collection. used by [[MongoCreate]] and
 * [[MongoUpdate]]
 */
private[mongo] trait MongoWrite[P <: Persistent] {
  repo: MongoRepo[P] =>

  protected lazy val persistentToCasbahTranslator =
    new PersistentToCasbahTranslator(subdomain.emblematic)

  protected def casbahForP(p: P): MongoDBObject = {
    anyToMongoDBObject(persistentToCasbahTranslator.translate(p, true)(pTypeKey))
  }

  protected def anyToMongoDBObject(any: Any): MongoDBObject =
    if (any.isInstanceOf[MongoDBObject]) any.asInstanceOf[MongoDBObject]
    else any.asInstanceOf[DBObject]

  protected def throwDuplicateKeyValException(p: P, cause: DuplicateKeyException): Unit = {
    val indexRegex = """index: (?:[\w\.]*\$)?(\S+)\s+dup key: \{ :""".r.unanchored
    val name = cause.getMessage match {
      case indexRegex(name) => name
      case _ => throw cause
    }
    val realizedKey = realizedPType.keySet.find(key => indexName(key) == name).getOrElse(throw cause)
    throw new DuplicateKeyValException(p, realizedKey.key, cause)
  }

}
