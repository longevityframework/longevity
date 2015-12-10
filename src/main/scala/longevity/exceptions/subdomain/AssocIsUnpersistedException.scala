package longevity.exceptions.subdomain

import longevity.subdomain._

/** thrown on attempt to retrieve a persisted aggregate from an unpersisted assoc */
class AssocIsUnpersistedException[R <: RootEntity](assoc: Assoc[R])
extends AssocException(assoc, "cannot retrieve a persisted aggregate from an unpersisted assoc")
