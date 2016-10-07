package longevity.exceptions.persistence.cassandra

/** an exception thrown when a Cassandra [[longevity.persistence.Repo
 * repository]] encounters a query that contains a `Query.all` expression
 */
class FilterAllInQueryException extends CassandraPersistenceException(
  s"Query.All operator is not supported in cassandra queries")
