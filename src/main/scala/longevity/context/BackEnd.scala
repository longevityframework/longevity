package longevity.context

/** the back end used by a longevity context. right now, you have
 * three options:
 *
 *   - [[InMem]]
 *   - [[Mongo]]
 *   - [[Cassandra]]
 * 
 * please note that the back end selected for your longevity context
 * can be overridden in a test environment, so that you can use an in-memory
 * database for integration testing.
 */
sealed trait BackEnd {

  /** the name of the back end */
  val name: String
}

/** a back end indicating that persistent objects live in-memory. when the
 * application exits, they are gone.
 */
case object InMem extends BackEnd {
  val name = "InMem"
}

/** a back end indicating that persistent objects live in MongoDB */
case object Mongo extends BackEnd {
  val name = "Mongo"
}

/** a back end indicating that persistent objects live in Cassandra */
case object Cassandra extends BackEnd {
  val name = "Cassandra"
}
