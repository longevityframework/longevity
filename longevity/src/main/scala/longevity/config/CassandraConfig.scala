package longevity.config

/** the Cassandra configuration
 *
 * @param keyspace the cassandra keyspace
 * @param address the contact point for the cassandra cluster
 * @param credentials optional username and password for connecting to the cassandra cluster
 * @param replicationFactor the replication factor to use when creating a keyspace
 * 
 * @see LongevityConfig
 */
case class CassandraConfig(
  keyspace: String,
  address: String,
  credentials: Option[DatabaseCredentials],
  replicationFactor: Int)
