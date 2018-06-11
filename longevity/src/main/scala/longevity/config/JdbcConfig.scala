package longevity.config

/** the JDBC configuration
 *
 * @param driverClass the name of the driver class. this should
 * always be "org.sqlite.JDBC", but we are leaving a back door here
 * for people who want to experiment with this back end using a
 * different JDBC driver
 * 
 * @param url the database url
 *
 * @see LongevityConfig
 */
case class JdbcConfig(driverClass: String, url: String)
