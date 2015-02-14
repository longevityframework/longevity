package longevity.testUtil

import longevity.domain.Entity
import longevity.domain.UnpersistedAssoc

/** An exception thrown by [[PersistedToUnpersistedTransformer]] when it encounters an
 * [[longevity.domain.Assoc]] that is not persisted. */
class EncounteredUnpersistedAssocException(assoc: UnpersistedAssoc[_ <: Entity])
extends Exception(s"encountered an unpersisted assoc: $assoc")
