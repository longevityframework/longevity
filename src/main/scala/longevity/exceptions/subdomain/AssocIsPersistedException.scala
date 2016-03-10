package longevity.exceptions.subdomain

import longevity.subdomain._

/** thrown on attempt to retrieve an unpersisted entity from a persisted assoc */
class AssocIsPersistedException(assoc: Assoc[_ <: Persistent])
extends AssocException(assoc, "cannot retrieve an unpersisted entity from a persisted assoc")
