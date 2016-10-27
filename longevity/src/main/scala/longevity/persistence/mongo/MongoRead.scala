package longevity.persistence.mongo

import org.bson.BsonDocument
import org.bson.BsonValue
import longevity.persistence.PState
import longevity.subdomain.Persistent
import longevity.subdomain.ptype.Prop

/** utilities for reading from a mongo collection. used by [[MongoRetrieve]] and
 * [[MongoQuery]]
 */
private[mongo] trait MongoRead[P <: Persistent] {
  repo: MongoRepo[P] =>

  private lazy val bsonToSubdomainTranslator =
    new BsonToSubdomainTranslator(subdomain.emblematic)

  protected def bsonToState(document: BsonDocument): PState[P] = {
    val id = document.getObjectId("_id").getValue
    val rv = if (document.isInt64("_rowVersion")) {
      Some(document.getInt64("_rowVersion").longValue)
    } else {
      None
    }
    val p  = bsonToSubdomainTranslator.translate(document)(pTypeKey)
    PState(MongoId[P](id), rv, p)
  }

  protected def propValToMongo[A](value: A, prop: Prop[_ >: P <: Persistent, A]): BsonValue = {
    subdomainToBsonTranslator.translate(value, false)(prop.propTypeKey)
  }

}
