package longevity.persistence.cassandra

import java.util.UUID
import longevity.persistence.DatabaseId

private[cassandra] case class CassandraId[P](uuid: UUID) extends DatabaseId[P] {
  private[longevity] val _lock = 0
  def widen[Q >: P] = CassandraId[Q](uuid)
}
