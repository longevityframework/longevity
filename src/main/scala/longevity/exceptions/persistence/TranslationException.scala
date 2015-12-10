package longevity.exceptions.persistence

import emblem.TypeKey
import emblem.exceptions.CouldNotTraverseException

/** an exception thrown by a [[longevity.persistence.Repo repository]] when translating between an
 * aggregate and a serialized form.
 */
class TranslationException(message: String, cause: Exception)
extends PersistenceException(message, cause)
