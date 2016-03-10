package longevity.exceptions.subdomain

import longevity.subdomain._

/** an exception with the usage of an [[longevity.subdomain.Assoc]] */
abstract class AssocException(val assoc: Assoc[_ <: Persistent], message: String)
extends SubdomainException(message)
