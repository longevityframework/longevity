package longevity.exceptions.subdomain

import longevity.subdomain._

/** an exception with the usage of an [[longevity.subdomain.Assoc]] */
abstract class AssocException[R <: Root](val assoc: Assoc[R], message: String)
extends SubdomainException(message)
