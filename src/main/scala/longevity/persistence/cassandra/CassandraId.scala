package longevity.persistence.cassandra

import java.util.UUID
import longevity.subdomain.persistent.Persistent
import longevity.persistence.PersistedAssoc

private[cassandra] case class CassandraId[P <: Persistent](uuid: UUID) extends PersistedAssoc[P] {
  private[longevity] val _lock = 0
}
