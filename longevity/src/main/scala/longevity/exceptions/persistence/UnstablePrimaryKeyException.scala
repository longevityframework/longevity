package longevity.exceptions.persistence

import longevity.model.KeyVal

/** an exception thrown on attempt to persist an object with a modified primary key */
class UnstablePrimaryKeyException[P](
  val orig: P,
  val origKeyVal: KeyVal[P],
  val newKeyVal: KeyVal[P])
extends PersistenceException(
  s"attempt to modify primary key of persistent object $orig from $origKeyVal to $newKeyVal")
