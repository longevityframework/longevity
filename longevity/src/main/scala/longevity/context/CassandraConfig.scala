package longevity.context

/** the cassandra configuration
 *
 * @see LongevityConfig
 */
case class CassandraConfig(
  keyspace: String,
  address: String,
  credentials: Option[DatabaseCredentials],
  replicationFactor: Int)
