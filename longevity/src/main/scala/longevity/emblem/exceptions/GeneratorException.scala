package longevity.emblem.exceptions

import typekey.TypeKey

/** an exception indicating you broke the contract of one of the
 * [[emblem.emblematic.Emblem]] or [[emblem.emblematic.Union]] factory methods
 * 
 * @param key the type of the emblem or union you were trying to build
 */
private[longevity] abstract class GeneratorException(val key: TypeKey[_], message: String) extends EmblemException(message)
