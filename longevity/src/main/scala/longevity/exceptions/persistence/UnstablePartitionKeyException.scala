package longevity.exceptions.persistence

import longevity.subdomain.AnyKeyVal

/** an exception thrown on attempt to persist an object with a modified partition key */
class UnstablePartitionKeyException[P](
  val orig: P,
  val origKeyVal: AnyKeyVal[P],
  val newKeyVal: AnyKeyVal[P])
extends PersistenceException(
  s"attempt to modify partition key of persistent object $orig from $origKeyVal to $newKeyVal")
