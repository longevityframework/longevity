package longevity.exceptions.persistence.cassandra

/** an exception thrown when a Cassandra [[longevity.persistence.Repo
 * repository]] encounters a query that contains an `order by` clause.
 *
 * once we implement partition indexes, we will support order by clauses for
 * Cassandra. but please be aware that Cassandra `ORDER BY` clauses are
 * limited to a single column, and it must be the second column in a compound
 * Cassandra `PRIMARY KEY`.
 *
 * @see https://docs.datastax.com/en/cql/3.1/cql/cql_reference/select_r.html
 * @see https://www.pivotaltracker.com/story/show/127406611
 */
class OrderByInQueryException extends CassandraPersistenceException(
  s"order by clauses are not currently supported in cassandra queries")
