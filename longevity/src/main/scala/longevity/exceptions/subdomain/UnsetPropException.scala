package longevity.exceptions.subdomain

import longevity.subdomain.root.Key
import longevity.subdomain.root.Prop
import longevity.subdomain.RootEntity

/** an exception indicating an attempt to build a KeyVal without having set all the properties
 *
 * @param key the key
 * @param props the properties that haven't been set
 */
class UnsetPropException[R <: RootEntity](val key: Key[R], val props: Seq[Prop[R, _]])
extends KeyValException(
  s"attempt to build a KeyVal for key $key with unset properties $props")
