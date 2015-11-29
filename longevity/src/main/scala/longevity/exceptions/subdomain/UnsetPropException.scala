package longevity.exceptions.subdomain

import longevity.subdomain.root.Key
import longevity.subdomain.root.Prop
import longevity.subdomain.RootEntity

/** an exception indicating that
 * [[longevity.subdomain.Key#ValBuilder.build]] was called before all of the nat key
 * properties have been set
 *
 * @param key the nat key
 * @param props the nat key props that haven't been set
 */
class UnsetPropException[E <: RootEntity](val key: Key[E], val props: Seq[Prop[E, _]])
extends KeyValBuilderException(
  s"Key.ValBuilder.build was called for key $key with unset properties $props")
