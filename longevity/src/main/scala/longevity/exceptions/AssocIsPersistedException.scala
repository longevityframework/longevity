package longevity.exceptions

import longevity.subdomain._

class AssocIsPersistedException[E <: RootEntity](assoc: Assoc[E])
extends AssocException(assoc, "cannot retrieve an unpersisted aggregate from a persisted assoc")
