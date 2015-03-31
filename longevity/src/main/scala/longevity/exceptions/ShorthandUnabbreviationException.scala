package longevity.exceptions

import emblem.TypeKey
import emblem.exceptions.ExtractorInverseException

/** an exception thrown when a [[longevity.persistence.MongoRepo MongoRepo]] cannot successfully translate
 * between an entity and a BSON `MongoDBObject`.
 */
class ShorthandUnabbreviationException(
  abbreviated: Any,
  actualTypeKey: TypeKey[_],
  cause: ExtractorInverseException)
extends MongoRepoException(
  s"could not unabbreviate $abbreviated into a $actualTypeKey",
  cause)
