package longevity.exceptions.persistence.cassandra

class AllInQueryException extends CassandraPersistenceException(
  s"Query.All operator is not supported in cassandra queries")
