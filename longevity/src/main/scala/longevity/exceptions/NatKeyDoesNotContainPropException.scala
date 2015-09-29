package longevity.exceptions

import longevity.subdomain.NatKey
import longevity.subdomain.NatKeyProp
import longevity.subdomain.RootEntity

/** an exception indicating that the [[longevity.subdomain.NatKeyProp natural key property]] passed to
 * either of the `setProp` methods in [[longevity.subdomain.NatKey#ValBuilder]] is not a part of the natural key
 * being built.
 *
 * @param key the nat key
 * @param prop the nat key prop
 */
class NatKeyDoesNotContainPropException[E <: RootEntity](key: NatKey[E], prop: NatKeyProp[E])
extends NatKeyValBuilderException(s"nat key prop $prop is not a part of natural key $key")
