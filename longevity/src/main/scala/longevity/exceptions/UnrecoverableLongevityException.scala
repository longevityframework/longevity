package longevity.exceptions

import journal.Logger

/** an exception that logs itself at warn level. this is for exceptions that
 * indicate API misuse. these exceptions are intended to be unrecoverable.
 */
trait UnrecoverableLongevityException {
  self: Throwable =>

  private val logger = Logger[this.type]

  logger.warn(getMessage, this)

}
