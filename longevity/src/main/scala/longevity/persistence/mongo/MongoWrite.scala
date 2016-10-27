package longevity.persistence.mongo

import com.mongodb.MongoWriteException
import longevity.exceptions.persistence.DuplicateKeyValException
import longevity.persistence.PState
import longevity.subdomain.Persistent
import org.bson.BsonDocument
import org.bson.BsonInt64
import org.bson.BsonObjectId

/** utilities for writing to a mongo collection. used by [[MongoCreate]] and
 * [[MongoUpdate]]
 */
private[mongo] trait MongoWrite[P <: Persistent] {
  repo: MongoRepo[P] =>

  protected lazy val subdomainToBsonTranslator =
    new SubdomainToBsonTranslator(subdomain.emblematic)

  protected def bsonForState(state: PState[P]): BsonDocument = {
    val document = translate(state.get)
    document.append("_id", new BsonObjectId(mongoId(state)))
    state.rowVersion.foreach { v =>
      document.append("_rowVersion", new BsonInt64(v))
    }
    document
  }

  protected def translate(p: P): BsonDocument =
    subdomainToBsonTranslator.translate(p, true)(pTypeKey).asDocument

  protected def throwDuplicateKeyValException(p: P, cause: MongoWriteException): Nothing = {
    val indexRegex = """index: (?:[\w\.]*\$)?(\S+)\s+dup key: \{ :""".r.unanchored
    val name = cause.getMessage match {
      case indexRegex(name) => name
      case _ => throw cause
    }
    val realizedKey = realizedPType.keySet.find(key => indexName(key) == name).getOrElse(throw cause)
    throw new DuplicateKeyValException(p, realizedKey.key, cause)
  }

  protected def mongoId(state: PState[P]) = state.id.asInstanceOf[MongoId[P]].objectId

}
