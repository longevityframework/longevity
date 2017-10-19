package longevity.exceptions.persistence.cassandra

/** an exception thrown when a Cassandra [[longevity.persistence.Repo repository]] receives a
 * request to unversion schema
 */
class UnversionSchemaException extends CassandraPersistenceException(
  s"Unversioning schema operation is not supported by cassandra back end")
