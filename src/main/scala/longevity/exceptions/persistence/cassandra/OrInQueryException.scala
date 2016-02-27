package longevity.exceptions.persistence.cassandra

class OrInQueryException extends CassandraPersistenceException(
  s"or operator is not supported in cassandra queries")
