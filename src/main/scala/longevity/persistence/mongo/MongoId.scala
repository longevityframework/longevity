package longevity.persistence.mongo

import com.mongodb.casbah.Imports.ObjectId
import longevity.persistence.DatabaseId
import longevity.subdomain.persistent.Persistent

private[persistence] case class MongoId[P <: Persistent](objectId: ObjectId)
extends DatabaseId[P] {
  private[longevity] val _lock = 0
  def widen[Q >: P <: Persistent] = MongoId[Q](objectId)
}
