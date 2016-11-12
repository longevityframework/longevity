package longevity.persistence.mongo

import longevity.persistence.DatabaseId
import org.bson.types.ObjectId

private[persistence] case class MongoId[P](objectId: ObjectId)
extends DatabaseId[P] {
  private[longevity] val _lock = 0
  def widen[Q >: P] = MongoId[Q](objectId)
}
