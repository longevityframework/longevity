package longevity.persistence.mongo

import com.mongodb.casbah.Imports.ObjectId
import longevity.persistence.PersistedAssoc
import longevity.subdomain.persistent.Persistent

private[persistence] case class MongoId[P <: Persistent](objectId: ObjectId)
extends PersistedAssoc[P] {
  private[longevity] val _lock = 0
}
