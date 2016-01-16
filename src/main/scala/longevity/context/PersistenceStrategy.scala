package longevity.context

/** the persistence strategy used by a longevity context. right now, you have three options:
 *
 *   - [[InMem]]
 *   - [[Mongo]]
 *   - [[Cassandra]]
 * 
 * please note that the persistence strategy selected for your longevity context can be overridden in a test
 * environment, so that you can use an in-memory database for integration testing on your mongo projects.
 */
sealed trait PersistenceStrategy

/** entities live in-memory. when the application exits, they are gone. */
sealed trait InMem extends PersistenceStrategy

case object InMem extends InMem

/** entities live in MongoDB */
sealed trait Mongo extends PersistenceStrategy

case object Mongo extends Mongo

/** entities live in Cassandra */
sealed trait Cassandra extends PersistenceStrategy

case object Cassandra extends Cassandra
