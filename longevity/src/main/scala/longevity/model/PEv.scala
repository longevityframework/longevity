package longevity.model

import emblem.TypeKey

/** evidence for a persistent class
 *
 * this evidence is provided in the persistent type (`PType`) for the same persistent class. because
 * the `PType` is typically the companion object for your persistent class, this evidence should be
 * available when needed.
 *
 * @tparam M the domain model
 * @tparam P the persistent class
 *
 * @see longevity.model.annotations.persistent
 * @see longevity.model.PType
 */
class PEv[M, P : TypeKey] private[longevity](private[longevity] val key: TypeKey[P])
