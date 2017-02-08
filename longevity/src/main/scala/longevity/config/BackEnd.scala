package longevity.config

/** the back end used by the longevity context. right now, you have
 * four options:
 *
 *   - [[Cassandra]]
 *   - [[InMem]]
 *   - [[MongoDB]]
 *   - [[SQLite]]
 * 
 * please note that the back end selected for your longevity context
 * can be overridden in a test environment, so that you can use an in-memory
 * database for integration testing.
 */
sealed trait BackEnd {

  /** the name of the back end */
  val name: String
}

/** contains a list of all the testable back ends */
private[longevity] object BackEnd {

  /** a list of all the supported back ends. this list notably leaves out [[JDBC]], as we have no way
   * to produce tests for this back end without a JDBC driver and a real database behind it
   */
  val values = Cassandra :: InMem :: MongoDB :: SQLite :: Nil

}

/** a back end indicating that persistent objects live in Cassandra */
case object Cassandra extends BackEnd {
  val name = "Cassandra"
}

/** a back end indicating that persistent objects live in-memory. when the
 * application exits, they are gone.
 * 
 * the current implementation is not designed to perform well in the face of
 * large datasets. it is a fully functional back end that can be used in
 * testing, when you don't want to deal with the hassle of connecting to a real
 * database, and cleaning up after your tests.
 */
case object InMem extends BackEnd {
  val name = "InMem"
}

/** a back end indicating that persistent objects live in MongoDB */
case object MongoDB extends BackEnd {
  val name = "MongoDB"
}

/** a back end indicating that persistent objects live in some unspecified database that we connect
  * to via some unspecified JDBC driver
 */
case object JDBC extends BackEnd {
  val name = "JDBC"
}

/** a back end indicating that persistent objects live in SQLite */
case object SQLite extends BackEnd {
  val name = "SQLite"
}
