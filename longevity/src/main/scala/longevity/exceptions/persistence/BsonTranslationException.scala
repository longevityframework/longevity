package longevity.exceptions.persistence

import emblem.TypeKey
import emblem.exceptions.TraversorException

/** an exception thrown when a [[longevity.persistence.Repo repository]] cannot successfully translate
 * between an entity and a BSON `MongoDBObject`.
 *
 * this kind of exception indicates a problem with one of your entities having a property that is not one of
 * the entities in the subdomain, mapped by a shorthand, or a basic value. there should be a unit test to
 * assure that your subdomain does not have these kinds of problems. see pt 91466438
 */
class BsonTranslationException(val typeKey: TypeKey[_], cause: TraversorException)
extends TranslationException(s"don't know how to translate type ${typeKey.tpe}", cause)
