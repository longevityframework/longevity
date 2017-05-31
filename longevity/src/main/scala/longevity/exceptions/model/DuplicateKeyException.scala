package longevity.exceptions.model

import emblem.TypeKey
import emblem.typeKey

/** thrown on attempt to construct a
 * [[longevity.model.PType persistent type]] with more than one
 * [[longevity.model.ptype.Key key]] for a single key value class
 */
class DuplicateKeyException[P : TypeKey, V : TypeKey] extends ModelTypeException(
  s"PType ${typeKey[P].name} contains multiple keys with same type ${typeKey[V].name}")
