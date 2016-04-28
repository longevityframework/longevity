package longevity.exceptions.subdomain.ptype

import emblem.TypeKey
import longevity.subdomain.persistent.Persistent

/** an exception thrown when certain [[longevity.subdomain.ptype.PType
 * persistent type]] functionality is invoked before the persistent type has
 * been associated with a [[longevity.subdomain.Subdomain]]
 */
class PTypeHasNoSubdomainException(
  pTypeKey: TypeKey[_ <: Persistent])(
  specificMessage: String =
    s"some persistent type functionality is not available without a subdomain.")
extends PTypeException(
  s"persistent type ${pTypeKey} is not associated with a subdomain. $specificMessage")
