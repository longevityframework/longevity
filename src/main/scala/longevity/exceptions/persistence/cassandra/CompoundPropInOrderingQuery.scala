package longevity.exceptions.persistence.cassandra

/** an exception thrown when a Cassandra [[longevity.persistence.Repo]]
 * encounters a query that contains an ordering expression (<, <=, >, >=) on a
 * property that is composed of more than a single basic value.
 *
 * Cassandra cannot handle such queries for the same reason that it cannot
 * handle queries with or expressions.
 */
class CompoundPropInOrderingQuery extends CassandraPersistenceException(
  s"cassandra does not support ordering (<, <=, >, >=) queries on compound properties")
