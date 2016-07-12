package longevity.exceptions.persistence.cassandra

/** an exception thrown when the
 * [[longevity.persistence.cassandra.CassandraRepo]] encounters a query that
 * contains a `Query.all` expression
 */
class AllInQueryException extends CassandraPersistenceException(
  s"Query.All operator is not supported in cassandra queries")
