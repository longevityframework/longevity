package longevity.persistence.sqlite

import java.util.UUID
import longevity.persistence.DatabaseId

private[sqlite] case class SQLiteId[P](uuid: UUID) extends DatabaseId[P] {
  private[longevity] val _lock = 0
  def widen[Q >: P] = SQLiteId[Q](uuid)
}
