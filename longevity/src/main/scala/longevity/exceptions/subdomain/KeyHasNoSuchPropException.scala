package longevity.exceptions.subdomain

import longevity.subdomain.RootEntity
import longevity.subdomain.root.Key
import longevity.subdomain.root.Prop

/** an exception indicating an attempt to create a key value using a property that does not belong to the key
 *
 * @param key the key
 * @param prop the key prop
 */
class KeyHasNoSuchPropException[R <: RootEntity](key: Key[R], prop: Prop[R, _])
extends KeyValException(
  s"property $prop is not a part of key $key")
