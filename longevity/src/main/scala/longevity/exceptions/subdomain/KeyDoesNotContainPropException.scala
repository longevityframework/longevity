package longevity.exceptions.subdomain

import longevity.subdomain.RootEntity
import longevity.subdomain.root.Key
import longevity.subdomain.root.Prop

/** an exception indicating that the [[longevity.subdomain.Prop property]] passed to
 * either of the `setProp` methods in [[longevity.subdomain.Key#ValBuilder]] is not a part of the natural key
 * being built.
 *
 * @param key the nat key
 * @param prop the nat key prop
 */
class KeyDoesNotContainPropException[R <: RootEntity](key: Key[R], prop: Prop[R, _])
extends KeyValBuilderException(s"nat key prop $prop is not a part of natural key $key")
