package longevity.exceptions

import longevity.subdomain.NatKeyProp
import longevity.subdomain.RootEntity
import longevity.subdomain.RootEntityType

/** an exception indicating that
 * [[longevity.subdomain.RootEntityType#NatKey#Builder.build]] was called before all of the nat key
 * properties have been set
 *
 * @param key the nat key
 * @param props the nat key props that haven't been set
 */
class UnsetNatKeyPropException[E <: RootEntity](
  val key: RootEntityType[E]#NatKey,
  val props: Set[NatKeyProp[E]])
extends NatKeyValBuilderException(
  s"NatKey.ValBuilder.build was called for key $key with unset properties $props")
