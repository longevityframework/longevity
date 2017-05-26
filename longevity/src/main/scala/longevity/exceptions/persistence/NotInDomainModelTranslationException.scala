package longevity.exceptions.persistence

import emblem.exceptions.TraversorException

/** an exception thrown by the [[longevity.persistence.Repo repository]] when
 * translating between a persistent and a serialized form, and a type is
 * encountered that is not a basic type or collection, and is not a
 * persistent, component, or key value defined in the domain model.
 *
 * ideally, we would catch this kind of problem as early as possible. it could
 * be in a unit test, or perhaps in a macro.
 *
 * TODO we might want to remove and undocument this. the new evidence setup avoids the need for this
 * (for the most part)
 */
class NotInDomainModelTranslationException(val typeName: String, cause: TraversorException)
extends TranslationException(s"don't know how to translate type $typeName", cause) {

  def this(typeName: String) { this(typeName, null) }

}
