package longevity.model

import typekey.TypeKey

/** evidence for a key value
 *
 * this evidence is provided in the key value type (`KVType`) for the same key value class. because
 * the `KVType` is typically the companion object for your key value class, this evidence should be
 * available when needed.
 *
 * @tparam M the domain model
 * @tparam P the persistent class
 * @tparam V the key value class
 * 
 * @see longevity.model.annotations.keyVal
 * @see longevity.model.KVType
 */
class KVEv[M : ModelEv, P, V] private[longevity](private[longevity] val key: TypeKey[V])
