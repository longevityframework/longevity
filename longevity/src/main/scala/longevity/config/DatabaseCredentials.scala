package longevity.config

/** cassandra database credentials
 *
 * @see CassandraConfig
 */
case class DatabaseCredentials(
  username: String,
  password: String)
