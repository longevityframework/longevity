package longevity.exceptions

import longevity.subdomain.NatKey
import longevity.subdomain.NatKeyProp
import longevity.subdomain.RootEntity

/** an exception indicating that
 * [[longevity.subdomain.NatKey#ValBuilder.build]] was called before all of the nat key
 * properties have been set
 *
 * @param key the nat key
 * @param props the nat key props that haven't been set
 */
class UnsetNatKeyPropException[E <: RootEntity](val key: NatKey[E], val props: Set[NatKeyProp[E]])
extends NatKeyValBuilderException(
  s"NatKey.ValBuilder.build was called for key $key with unset properties $props")
