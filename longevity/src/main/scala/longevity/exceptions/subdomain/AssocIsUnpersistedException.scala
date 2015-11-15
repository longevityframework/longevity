package longevity.exceptions.subdomain

import longevity.subdomain._

/** thrown on attempt to retrieve a persisted aggregate from an unpersisted assoc */
class AssocIsUnpersistedException[E <: RootEntity](assoc: Assoc[E])
extends AssocException(assoc, "cannot retrieve a persisted aggregate from an unpersisted assoc")
