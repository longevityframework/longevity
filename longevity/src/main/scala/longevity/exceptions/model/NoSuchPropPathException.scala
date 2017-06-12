package longevity.exceptions.model

import typekey.TypeKey

/** an exception indicating an attempt to create a property with a prop path
 * that does not exist
 *
 * @param path the requested property path
 * @param pTypeKey the type of the persistent with the problematic property
 */
class NoSuchPropPathException(
  val path: String,
  val pTypeKey: TypeKey[_])
extends ModelTypeException(
  s"no such property path '$path` in persistent ${pTypeKey.name}")

