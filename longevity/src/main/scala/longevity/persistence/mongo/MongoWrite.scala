package longevity.persistence.mongo

import com.mongodb.MongoWriteException
import com.mongodb.client.model.Filters
import longevity.exceptions.persistence.DuplicateKeyValException
import longevity.persistence.PState
import longevity.subdomain.KeyVal
import longevity.subdomain.Persistent
import longevity.subdomain.realized.RealizedKey
import org.bson.BsonDocument
import org.bson.BsonInt64
import org.bson.BsonObjectId

/** utilities for writing to a mongo collection. used by [[MongoCreate]] and
 * [[MongoUpdate]]
 */
private[mongo] trait MongoWrite[P <: Persistent] {
  repo: MongoRepo[P] =>

  protected lazy val subdomainToBsonTranslator = new SubdomainToBsonTranslator(subdomain.emblematic)

  /** BSON for a persistent state. this puts the partition key in the `_id`
   * column, which may or may not be the best choice. alternative is to just put
   * in an `ObjectId`, either chosen here, or chosen by mongoDB if we leave out
   * the column here. the document is going to have the `_id` column whatever we
   * do.
   */
  protected def bsonForState(state: PState[P]): BsonDocument = {
    val document = translate(state.get)
    document.append("_id", idBson(state))
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

  protected def mongoId(state: PState[P]) = state.id.map(_.asInstanceOf[MongoId[P]].objectId)

  /** a query that identifies the document to update or delete */
  protected def writeQuery(state: PState[P]) = {
    val keyFilter = Filters.eq("_id", idBson(state))
    if (persistenceConfig.optimisticLocking) {
      val rvBson = state.rowVersion match {
        case Some(rv) => Filters.eq("_rowVersion", new BsonInt64(rv))
        case None => Filters.exists("_rowVersion", false)
      }
      Filters.and(keyFilter, rvBson)
    } else {
      keyFilter
    }
  }

  private def idBson(state: PState[P]) = {
    if (realizedPType.partitionKey.isEmpty) {
      new BsonObjectId(mongoId(state).get)
    } else {
      def keyValBson[V <: KeyVal[P, V]](key: RealizedKey[P, V]) = {
        val keyVal = key.keyValForP(state.get)
        val fieldName = key.realizedProp.inlinedPath
        subdomainToBsonTranslator.translate(keyVal, false)(key.keyValTypeKey)
      }
      keyValBson(realizedPType.partitionKey.get)
    }
  }

}
