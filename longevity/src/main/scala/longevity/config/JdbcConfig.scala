package longevity.config

/** the JDBC configuration
 *
 * @param driverClass the name of the driver class. this should always be "org.sqlite.JDBC", but we
 * are leaving a back door here for people who want to experiment with this back end using a
 * different JDBC driver
 * 
 * @param url the database url
 *
 * @param synchronized if true, run all database queries synchronized to the JDBC connection. most
 * applications will want to set this to `false`, to avoid the overhead of synchronization. But for
 * certain JDBC drivers, such as SQLite, and highly parallel database interaction, such as the
 * longevity test suite, this can avoid deadlock situations.
 * 
 * @see LongevityConfig
 */
case class JdbcConfig(driverClass: String, url: String, synchronized: Boolean)
