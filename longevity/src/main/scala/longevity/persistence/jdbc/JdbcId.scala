package longevity.persistence.jdbc

import java.util.UUID
import longevity.persistence.DatabaseId

private[jdbc] case class JdbcId[P](uuid: UUID) extends DatabaseId[P] {
  private[longevity] val _lock = 0
  def widen[Q >: P] = JdbcId[Q](uuid)
}
