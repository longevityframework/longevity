package longevity.exceptions.persistence.cassandra

class NeqInQueryException extends CassandraPersistenceException(
  s"not-equal operator is not supported in cassandra queries")
