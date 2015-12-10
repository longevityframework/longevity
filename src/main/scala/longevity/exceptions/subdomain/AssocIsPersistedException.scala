package longevity.exceptions.subdomain

import longevity.subdomain._

/** thrown on attempt to retrieve an unpersisted aggregate from a persisted assoc */
class AssocIsPersistedException[R <: RootEntity](assoc: Assoc[R])
extends AssocException(assoc, "cannot retrieve an unpersisted aggregate from a persisted assoc")
