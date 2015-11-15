package longevity.exceptions.subdomain

import longevity.subdomain.Key
import longevity.subdomain.KeyProp
import longevity.subdomain.RootEntity

/** an exception indicating that the [[longevity.subdomain.KeyProp natural key property]] passed to
 * either of the `setProp` methods in [[longevity.subdomain.Key#ValBuilder]] is not a part of the natural key
 * being built.
 *
 * @param key the nat key
 * @param prop the nat key prop
 */
class KeyDoesNotContainPropException[E <: RootEntity](key: Key[E], prop: KeyProp[E])
extends KeyValBuilderException(s"nat key prop $prop is not a part of natural key $key")
