package longevity.exceptions

import longevity.domain._

class AssocIsUnpersistedException[E <: RootEntity](assoc: Assoc[E])
extends AssocException(assoc, "cannot retrieve a persisted root from an unpersisted assoc")
