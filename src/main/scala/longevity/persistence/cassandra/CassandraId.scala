package longevity.persistence.cassandra

import emblem.TypeKey
import java.util.UUID
import longevity.subdomain.Root
import longevity.persistence.PersistedAssoc

// TODO do we need the type key here? check MongoId as well
private[cassandra] case class CassandraId[R <: Root](
  uuid: UUID,
  associateeTypeKey: TypeKey[R])
extends PersistedAssoc[R] {
  private[longevity] val _lock = 0
}
