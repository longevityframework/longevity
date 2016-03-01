package longevity.persistence.cassandra

import java.util.UUID
import longevity.subdomain.Root
import longevity.persistence.PersistedAssoc

private[cassandra] case class CassandraId[R <: Root](uuid: UUID) extends PersistedAssoc[R] {
  private[longevity] val _lock = 0
}
