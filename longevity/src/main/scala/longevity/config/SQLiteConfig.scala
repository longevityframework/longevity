package longevity.config

/** the SQLite configuration
 *
 * @param jdbcDriverClass the name of the driver class. this should
 * always be "org.sqlite.JDBC", but we are leaving a back door here
 * for people who want to experiment with this back end using a
 * different JDBC driver
 * 
 * @param jdbcUrl the JDBC database url
 *
 * @see LongevityConfig
 */
case class SQLiteConfig(jdbcDriverClass: String, jdbcUrl: String)
