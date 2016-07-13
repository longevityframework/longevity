package longevity.exceptions.persistence

import emblem.exceptions.TraversorException

/** an exception thrown by a [[longevity.persistence.Repo repository]] when
 * translating between a persistent and a serialized form, and a type is
 * encountered that is not a basic type or collection, and is not a
 * persistent, entity, or key value defined by the subdomain.
 *
 * ideally, we would catch this kind of problem as early as possible. it could
 * be in a unit test, or perhaps in a macro. see pt 91466438
 */
class NotInSubdomainTranslationException(val typeName: String, cause: TraversorException)
extends TranslationException(s"don't know how to translate type $typeName", cause) {

  def this(typeName: String) { this(typeName, null) }

}
