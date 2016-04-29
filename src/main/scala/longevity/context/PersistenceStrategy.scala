package longevity.context

/** the persistence strategy used by a longevity context. right now, you have
 * three options:
 *
 *   - [[InMem]]
 *   - [[Mongo]]
 *   - [[Cassandra]]
 * 
 * please note that the persistence strategy selected for your longevity context
 * can be overridden in a test environment, so that you can use an in-memory
 * database for integration testing.
 */
sealed trait PersistenceStrategy {

  /** the name of the persistence strategy */
  val name: String
}

/** entities live in-memory. when the application exits, they are gone. */
sealed trait InMem extends PersistenceStrategy {
  val name = "InMem"
}

case object InMem extends InMem

/** entities live in MongoDB */
sealed trait Mongo extends PersistenceStrategy {
  val name = "Mongo"
}

case object Mongo extends Mongo

/** entities live in Cassandra */
sealed trait Cassandra extends PersistenceStrategy {
  val name = "Cassandra"
}

case object Cassandra extends Cassandra
