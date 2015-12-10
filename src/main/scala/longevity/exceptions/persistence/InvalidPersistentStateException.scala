package longevity.exceptions.persistence

/** attempt to use the wrong kind of [[longevity.persistence.PersistentState]] */
class InvalidPersistentStateException(expected: String, actual: String)
extends PersistenceException(
  s"attempt to use $actual PersistentState where $expected is needed")
