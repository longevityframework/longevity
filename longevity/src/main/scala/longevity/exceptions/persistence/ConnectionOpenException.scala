package longevity.exceptions.persistence

/** an exception thrown when trying to open a connection that is already open */
class ConnectionOpenException extends PersistenceException("connection is already open")
