package longevity.exceptions.persistence.cassandra

import com.datastax.driver.core.exceptions.InvalidQueryException
import longevity.config.CassandraConfig

/** thrown on attempt to do persistence operations against a
 * [[longevity.config.Cassandra Cassandra]] keyspace that does not exist
 */
class KeyspaceDoesNotExistException(config: CassandraConfig, cause: InvalidQueryException)
extends CassandraPersistenceException(
  s"Cassandra keyspace ${config.keyspace} does not exist. " +
  "Perhaps you forgot to call `Repo.createSchema()`?",
  cause)
