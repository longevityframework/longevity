package longevity.persistence.mongo

import com.mongodb.DBObject
import com.mongodb.casbah.commons.Implicits.wrapDBObj
import longevity.persistence.PState
import longevity.subdomain.persistent.Persistent
import longevity.subdomain.ptype.Prop
import org.bson.types.ObjectId

/** utilities for reading from a mongo collection. used by [[MongoCreate]] and
 * [[MongoUpdate]]
 */
private[mongo] trait MongoRead[P <: Persistent] {
  repo: MongoRepo[P] =>

  private lazy val casbahToPersistentTranslator =
    new CasbahToPersistentTranslator(subdomain.emblematic)

  protected def dbObjectToPState(dbObject: DBObject): PState[P] = {
    val id = dbObject.getAs[ObjectId]("_id").get
    val p = casbahToPersistentTranslator.translate(dbObject)(pTypeKey)
    new PState[P](MongoId(id), p)
  }

  protected def propValToMongo[A](value: A, prop: Prop[_ >: P <: Persistent, A]): Any = {
    persistentToCasbahTranslator.translate(value, false)(prop.propTypeKey)
  }

}
