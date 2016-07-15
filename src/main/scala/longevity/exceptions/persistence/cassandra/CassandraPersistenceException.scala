package longevity.exceptions.persistence.cassandra

import longevity.exceptions.persistence.PersistenceException

/** an exception involving persistence using [[longevity.context.Cassandra
 * Cassandra persistence strategy]]
 */
class CassandraPersistenceException(message: String, cause: Exception)
extends PersistenceException(message, cause) {

  def this(message: String) { this(message, null) }

}