package longevity.exceptions

import longevity.subdomain._

/** an exception with the usage of an [[longevity.subdomain.Assoc]] */
abstract class AssocException[E <: RootEntity](val assoc: Assoc[E], message: String)
extends LongevityException(message)
