package longevity.exceptions.persistence.cassandra

import longevity.exceptions.UnrecoverableLongevityException
import longevity.exceptions.persistence.PersistenceException

/** an exception involving persistence using [[longevity.config.Cassandra
 * Cassandra back end]]
 */
class CassandraPersistenceException(message: String, cause: Exception)
extends PersistenceException(message, cause)
with UnrecoverableLongevityException {

  def this(message: String) { this(message, null) }

}
