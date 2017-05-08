package longevity.exceptions.persistence

import longevity.exceptions.UnrecoverableLongevityException

/** an exception thrown by a [[longevity.persistence.Repo repository]] when
 * translating between a persistent and a serialized form
 */
class TranslationException(message: String, cause: Exception)
extends PersistenceException(message, cause)
with UnrecoverableLongevityException {

  def this(message: String) { this(message, null) }

}
