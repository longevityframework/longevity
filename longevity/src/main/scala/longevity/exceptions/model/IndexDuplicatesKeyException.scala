package longevity.exceptions.model

import emblem.TypeKey

/** thrown on attempt to construct a
 * [[longevity.model.ModelType ModelType]] with a
 * [[longevity.model.PType PType]] that has a key and an index
 * defined over the same properties
 */
class IndexDuplicatesKeyException(
  val pTypeKey: TypeKey[_])
extends ModelTypeException(
  s"PType ${pTypeKey.name} has duplicate keys or indexes")
