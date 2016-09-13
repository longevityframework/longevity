package longevity.exceptions

import com.typesafe.scalalogging.LazyLogging

/** an exception that logs itself at warn level. this is for exceptions that
 * indicate API misuse. these exceptions are intended to be unrecoverable.
 */
trait UnrecoverableLongevityException extends LazyLogging {
  self: Throwable =>

  logger.warn(getMessage, this)

}
