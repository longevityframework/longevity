package longevity.persistence.cassandra

import java.util.UUID
import longevity.persistence.DatabaseId

private[cassandra] case class CassandraId(uuid: UUID) extends DatabaseId {
  private[longevity] val _lock = 0
}
