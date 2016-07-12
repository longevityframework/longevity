package longevity.subdomain

import longevity.subdomain.persistent.Persistent
import longevity.subdomain.ptype.Key

/** a key value.
 *
 * use this abstract class to extend the case class you want to use as a key
 * value.
 *
 * @tparam P the persistent type
 * @tparam V the key value type. a
 * [[http://ktoso.github.io/scala-types-of-types/#self-recursive-type
 * self-recursive type]]
 * 
 * @param key the key that this is a value for
 */
abstract class KeyVal[P <: Persistent, V <: KeyVal[P, V]](val key: Key[P, V])
