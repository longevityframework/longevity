package longevity.exceptions.persistence.cassandra

/** an exception thrown when a Cassandra [[longevity.persistence.RepoPool
 * repository]] encounters a query that contains a `FilterAll` expression
 */
class FilterAllInQueryException extends CassandraPersistenceException(
  s"FilterAll operator is not supported in cassandra queries")
