package longevity.persistence.mongo

import longevity.persistence.DatabaseId
import org.bson.types.ObjectId

private[persistence] case class MongoId(objectId: ObjectId) extends DatabaseId {
  private[longevity] val _lock = 0
}
