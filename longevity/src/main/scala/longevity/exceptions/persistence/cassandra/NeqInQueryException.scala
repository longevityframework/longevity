package longevity.exceptions.persistence.cassandra

/** an exception thrown when a Cassandra [[longevity.persistence.RepoPool
 * repository]] encounters a query that contains a `neq` expression
 */
class NeqInQueryException extends CassandraPersistenceException(
  s"not-equal operator is not supported in cassandra queries")
