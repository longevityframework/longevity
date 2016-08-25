package emblem

import org.joda.time.format.DateTimeFormat

/** general purpose functions for working with JSON */
object jsonUtil {

  /** the json4s date-time formatter used by
   * [[emblem.emblematic.traversors.sync.JsonToEmblematicTranslator]] and
   * [[emblem.emblematic.traversors.sync.EmblematicToJsonTranslator]]. this
   * format is entirely lossless AFAIK, including with respect to time zones,
   * and conforms to ISO 8061.
   */
  val dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS z").withOffsetParsed

}
