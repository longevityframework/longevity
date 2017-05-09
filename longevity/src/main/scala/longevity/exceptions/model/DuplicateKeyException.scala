package longevity.exceptions.model

import emblem.TypeKey
import emblem.typeKey
import longevity.model.KeyVal

/** thrown on attempt to construct a
 * [[longevity.model.PType persistent type]] with more than one
 * [[longevity.model.ptype.Key key]] for a single kind of
 * [[longevity.model.KeyVal KeyVal]]
 */
class DuplicateKeyException[P : TypeKey, V <: KeyVal[P] : TypeKey]
extends ModelTypeException(
  s"PType ${typeKey[P].name} contains multiple keys with same type ${typeKey[V].name}")
