package longevity.persistence.mongo

import com.mongodb.casbah.Imports.ObjectId
import longevity.persistence.PersistedAssoc
import longevity.subdomain.Root

private[persistence] case class MongoId[R <: Root](objectId: ObjectId) extends PersistedAssoc[R] {
  private[longevity] val _lock = 0
}
