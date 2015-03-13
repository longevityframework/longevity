package longevity.exceptions

import longevity.domain._

class AssocIsPersistedException[E <: RootEntity](assoc: Assoc[E])
extends AssocException(assoc, "cannot retrieve an unpersisted aggregate from a persisted assoc")
