package longevity.model

import emblem.TypeKey

/** evidence for a key value
 *
 * @tparam M the domain model
 * @tparam P the persistent class
 * @tparam V the key value class
 */
class KVEv[M : ModelEv, P, V] private[longevity](private[longevity] val key: TypeKey[V])
