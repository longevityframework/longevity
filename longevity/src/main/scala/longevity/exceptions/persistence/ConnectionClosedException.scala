package longevity.exceptions.persistence

/** an exception thrown when trying to work with a Repo when the connection is closed */
class ConnectionClosedException extends PersistenceException("connection is closed")
