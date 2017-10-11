package longevity.persistence.jdbc

import java.util.UUID
import longevity.persistence.DatabaseId

private[jdbc] case class JdbcId(uuid: UUID) extends DatabaseId {
  private[longevity] val _lock = 0
}
