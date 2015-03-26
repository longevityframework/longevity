package longevity.exceptions

import emblem.TypeKey
import emblem.exceptions.CouldNotTraverseException

/** an exception thrown when a [[MongoRepo]] cannot successfully translate between an entity and a
 * BSON `MongoDBObject`
 */
class CouldNotTranslateException(val typeKey: TypeKey[_], cause: CouldNotTraverseException)
extends LongevityException(s"don't know how to translate type ${typeKey.tpe}", cause)
