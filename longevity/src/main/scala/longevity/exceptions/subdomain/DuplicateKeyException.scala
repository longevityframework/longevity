package longevity.exceptions.subdomain

import emblem.TypeKey
import emblem.typeKey
import longevity.model.KeyVal

/** thrown on attempt to construct a
 * [[longevity.subdomain.PType persistent type]] with more than one
 * [[longevity.subdomain.ptype.Key key]] for a single kind of
 * [[longevity.subdomain.KeyVal KeyVal]]
 */
class DuplicateKeyException[P : TypeKey, V <: KeyVal[P] : TypeKey]
extends SubdomainException(
  s"PType ${typeKey[P].name} contains multiple keys with same type ${typeKey[V].name}")
