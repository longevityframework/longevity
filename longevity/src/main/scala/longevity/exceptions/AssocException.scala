package longevity.exceptions

import longevity.domain._

/** an exception with the usage of an [[Assoc]] */
abstract class AssocException[E <: RootEntity](
  val assoc: Assoc[E],
  message: String)
extends Exception(message)
