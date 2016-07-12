package longevity.subdomain

import longevity.subdomain.persistent.Persistent
import longevity.subdomain.ptype.Key

// TODO revisit scaladoc
/** a key value.
 *
 * use this abstract class to extend the case class you want to use as a key
 * value.
 *
 * @tparam P the persistent type
 * @param key the key that this is a value for. this key should really be a
 * `Key[P, Self]`, where `Self` is the case class extending `KeyVal`, but
 * I'm not going to try to enforce that now. it makes the typing nasty hard
 */
abstract class KeyVal[P <: Persistent, KV <: KeyVal[P, KV]](val key: Key[P, KV])
