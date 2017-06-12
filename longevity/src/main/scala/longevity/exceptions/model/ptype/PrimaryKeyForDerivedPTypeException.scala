package longevity.exceptions.model.ptype

import typekey.TypeKey

/** an exception thrown when [[longevity.model.DerivedPType derived
 * persistent type]] contains a [[longevity.model.ptype.PrimaryKey
 * primary key]]
 */
class PrimaryKeyForDerivedPTypeException[P : TypeKey] extends PTypeException(
  s"DerivedPType ${implicitly[TypeKey[P]].name} declares a primary key")
