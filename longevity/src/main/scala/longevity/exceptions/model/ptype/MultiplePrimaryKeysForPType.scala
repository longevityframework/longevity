package longevity.exceptions.model.ptype

import emblem.TypeKey

/** an exception thrown when [[longevity.model.PType persistent type]]
 * contains more than one [[longevity.model.ptype.PrimaryKey primary key]]
 */
class MultiplePrimaryKeysForPType[P : TypeKey] extends PTypeException(
  s"PType ${implicitly[TypeKey[P]].name} declares more than one primary key")
