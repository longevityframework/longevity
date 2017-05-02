package longevity.exceptions.persistence.cassandra

/** an exception thrown when a Cassandra [[longevity.persistence.OldRepo
 * repository]] encounters a query that contains an `or` expression
 */
class OrInQueryException extends CassandraPersistenceException(
  s"or operator is not supported in cassandra queries")
