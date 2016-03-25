package longevity.exceptions.subdomain

import longevity.subdomain.Assoc
import longevity.subdomain.persistent.Persistent

/** an exception with the usage of an [[longevity.subdomain.Assoc]] */
abstract class AssocException(val assoc: Assoc[_ <: Persistent], message: String)
extends SubdomainException(message)
