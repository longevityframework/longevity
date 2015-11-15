package longevity.exceptions.subdomain

import longevity.subdomain._

/** an exception with the usage of an [[longevity.subdomain.Assoc]] */
abstract class AssocException[E <: RootEntity](val assoc: Assoc[E], message: String)
extends SubdomainException(message)
