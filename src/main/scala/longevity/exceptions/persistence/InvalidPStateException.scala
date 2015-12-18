package longevity.exceptions.persistence

/** attempt to use the wrong kind of [[longevity.persistence.PState]] */
class InvalidPStateException(expected: String, actual: String)
extends PersistenceException(
  s"attempt to use $actual PState where $expected is needed")
