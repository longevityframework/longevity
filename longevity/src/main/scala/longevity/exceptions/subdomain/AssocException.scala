package longevity.exceptions.subdomain

import longevity.subdomain._

/** an exception with the usage of an [[longevity.subdomain.Assoc]] */
abstract class AssocException[R <: RootEntity](val assoc: Assoc[R], message: String)
extends SubdomainException(message)
