package longevity.config

/** the Cassandra configuration
 *
 * @param autoCreateKeyspace should longevity automatically create the keyspace if it doesn't
 * already exist when the connection is opened?
 * @param keyspace the cassandra keyspace
 * @param address the contact point for the cassandra cluster
 * @param credentials optional username and password for connecting to the cassandra cluster
 * @param replicationFactor the replication factor to use when creating a keyspace
 * 
 * @see LongevityConfig
 */
case class CassandraConfig(
  autoCreateKeyspace: Boolean,
  keyspace: String,
  address: String,
  credentials: Option[DatabaseCredentials],
  replicationFactor: Int)
