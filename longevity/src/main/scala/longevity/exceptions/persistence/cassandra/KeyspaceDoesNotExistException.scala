package longevity.exceptions.persistence.cassandra

import com.datastax.driver.core.exceptions.InvalidQueryException
import longevity.context.CassandraConfig

/** an exception involving persistence using [[longevity.context.Cassandra
 * Cassandra back end]]
 */
class KeyspaceDoesNotExistException(config: CassandraConfig, cause: InvalidQueryException)
extends CassandraPersistenceException(
  s"Cassandra keyspace ${config.keyspace} does not exist. " +
  "Perhaps you forgot to call `RepoPool.createSchema()`?",
  cause)
