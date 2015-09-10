package longevity.exceptions

import longevity.subdomain.RootEntity
import longevity.subdomain.RootEntityType

// PLEASE NOTE that all of these `NatKeyValBuilderExceptions` could be typed better if i was willing to drop
// them inside of `RootEntityType`. at the moment i am still clinging to the idea that exceptions should live
// top-level in package `longevity.exceptions`.

/** an exception indicating that
 * [[longevity.subdomain.RootEntityType#NatKey#Builder.build]] was called before all of the nat key
 * properties have been set
 *
 * @param key the nat key
 * @param props the nat key props that haven't been set
 */
class UnsetNatKeyPropException[E <: RootEntity](
  val key: RootEntityType[E]#NatKey,
  val props: Set[R] forSome { type R <: RootEntityType[E]#NatKeyProp })
extends NatKeyValBuilderException(
  s"NatKey.ValBuilder.build was called for key $key with unset properties $props")
