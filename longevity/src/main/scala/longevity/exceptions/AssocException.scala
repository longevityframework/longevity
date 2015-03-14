package longevity.exceptions

import longevity.domain._

/** an exception with the usage of an [[longevity.domain.Assoc]] */
abstract class AssocException[E <: RootEntity](
  val assoc: Assoc[E],
  message: String)
extends LongevityException(message)
