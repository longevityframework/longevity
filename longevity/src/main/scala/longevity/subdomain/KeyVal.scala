package longevity.subdomain

/** a key value.
 *
 * use this trait to extend the case class you want to use as a key
 * value.
 *
 * @tparam P the persistent type
 * @tparam V the key value type. a
 * [[http://ktoso.github.io/scala-types-of-types/#self-recursive-type
 * self-recursive type]]
 */
trait KeyVal[P <: Persistent, V <: KeyVal[P, V]]
