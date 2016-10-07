package longevity.exceptions.persistence

/** an exception that occurs when a [[longevity.subdomain.query.Query Query]]
 * cannot be handled by the [[longevity.persistence.Repo repository]].
 *
 * cassandra repositories cannot handle all kinds of queries, as cassandra CQL
 * queries have some rather strict limitations. mongo and in-memory repositories
 * can handle all queries.
 */
class QueryException(message: String) extends PersistenceException(message)
