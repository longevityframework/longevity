package longevity.exceptions.persistence

/** an exception thrown on attempt to persist an object with a modified primary key */
class UnstablePrimaryKeyException[P, V](val orig: P, val origKeyVal: V, val newKeyVal: V)
extends PersistenceException(
  s"attempt to modify primary key of persistent object $orig from $origKeyVal to $newKeyVal")
