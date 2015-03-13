package longevity.exceptions

import longevity.domain._

class AssocIsUnpersistedException[E <: RootEntity](assoc: Assoc[E])
extends AssocException(assoc, "cannot retrieve a persisted aggregate from an unpersisted assoc")
