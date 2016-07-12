package longevity.exceptions.persistence.cassandra

/** an exception thrown when the
 * [[longevity.persistence.cassandra.CassandraRepo]] encounters a query that
 * contains a `neq` expression
 */
class NeqInQueryException extends CassandraPersistenceException(
  s"not-equal operator is not supported in cassandra queries")
