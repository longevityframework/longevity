package longevity.exceptions.persistence.cassandra

/** an exception thrown when the
 * [[longevity.persistence.cassandra.CassandraRepo]] encounters a query that
 * contains an `or` expression
 */
class OrInQueryException extends CassandraPersistenceException(
  s"or operator is not supported in cassandra queries")
