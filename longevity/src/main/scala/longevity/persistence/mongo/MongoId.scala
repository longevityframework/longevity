package longevity.persistence.mongo

import longevity.persistence.DatabaseId
import longevity.subdomain.Persistent
import org.bson.types.ObjectId

private[persistence] case class MongoId[P <: Persistent](objectId: ObjectId)
extends DatabaseId[P] {
  private[longevity] val _lock = 0
  def widen[Q >: P <: Persistent] = MongoId[Q](objectId)
}
