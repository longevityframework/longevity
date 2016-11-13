package longevity.exceptions.persistence

import longevity.subdomain.KeyVal

/** an exception thrown on attempt to persist an object with a modified partition key */
class UnstablePartitionKeyException[P](
  val orig: P,
  val origKeyVal: KeyVal[P],
  val newKeyVal: KeyVal[P])
extends PersistenceException(
  s"attempt to modify partition key of persistent object $orig from $origKeyVal to $newKeyVal")
