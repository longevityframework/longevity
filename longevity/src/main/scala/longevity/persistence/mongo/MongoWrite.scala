package longevity.persistence.mongo

import com.mongodb.MongoWriteException
import com.mongodb.client.model.Filters
import longevity.exceptions.persistence.DuplicateKeyValException
import longevity.persistence.PState
import longevity.model.realized.RealizedKey
import org.bson.BsonDocument
import org.bson.BsonDateTime
import org.bson.BsonInt64
import org.bson.BsonObjectId

/** utilities for writing to a mongo collection. used by [[MongoCreate]] and
 * [[MongoUpdate]]
 */
private[mongo] trait MongoWrite[F[_], M, P] {
  repo: MongoPRepo[F, M, P] =>

  protected lazy val domainModelToBsonTranslator = new DomainModelToBsonTranslator(modelType.emblematic)

  /** BSON for a persistent state. this puts the primary key in the `_id`
   * column, which may or may not be the best choice. alternative is to just put
   * in an `ObjectId`, either chosen here, or chosen by mongoDB if we leave out
   * the column here. the document is going to have the `_id` column whatever we
   * do.
   */
  protected def bsonForState(state: PState[P]): BsonDocument = {
    val document = translate(state.get)
    if (!hasPrimaryKey) {
      document.append("_id", idBson(state))
    }
    state.rowVersion.foreach { v =>
      document.append("_rowVersion", new BsonInt64(v))
    }
    state.createdTimestamp.foreach { d =>
      document.append("_createdTimestamp", new BsonDateTime(d.getMillis))
    }
    state.updatedTimestamp.foreach { d =>
      document.append("_updatedTimestamp", new BsonDateTime(d.getMillis))
    }
    document
  }

  protected def translate(p: P): BsonDocument =
    domainModelToBsonTranslator.translate(p, true)(pTypeKey).asDocument

  protected def throwDuplicateKeyValException(p: P, cause: MongoWriteException): Nothing = {
    val indexRegex = """index: (?:[\w\.]*\$)?(\S+)\s+dup key: \{ :""".r.unanchored
    val name = cause.getMessage match {
      case indexRegex(name) => name
      case _ => throw cause
    }
    val realizedKey = realizedPType.keySet.find(key => indexName(key) == name).getOrElse(throw cause)
    throw new DuplicateKeyValException(p, realizedKey.key, cause)
  }

  protected def mongoId(state: PState[P]) = state.id.map(_.asInstanceOf[MongoId].objectId)

  /** a query that identifies the document to update or delete */
  protected def writeQuery(state: PState[P]) = {
    if (persistenceConfig.optimisticLocking) {
      val rvBson = state.rowVersion match {
        case Some(rv) => Filters.eq("_rowVersion", new BsonInt64(rv))
        case None => Filters.exists("_rowVersion", false)
      }
      Filters.and(keyFilter(state), rvBson)
    } else {
      keyFilter(state)
    }
  }

  private def keyFilter(state: PState[P]) = {
    realizedPType.primaryKey match {
      case Some(key) =>
        def pkFilter[V](key: RealizedKey[M, P, V]) = {
          val fieldName = key.realizedProp.inlinedPath
          val keyVal = key.keyValForP(state.get)
          val bson = domainModelToBsonTranslator.translate(keyVal, false)(key.keyValTypeKey)
          Filters.eq(fieldName, bson)
        }
        pkFilter(key)
      case None =>
        Filters.eq("_id", idBson(state))
    }
  }

  private def idBson(state: PState[P]) = new BsonObjectId(mongoId(state).get)

}
