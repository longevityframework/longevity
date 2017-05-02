package longevity.exceptions.persistence.cassandra

/** an exception thrown when a Cassandra [[longevity.persistence.OldRepo
 * repository]] encounters a query that contains an `offset` clause
 */
class OffsetInQueryException extends CassandraPersistenceException(
  s"offset clauses are not supported in cassandra queries")
