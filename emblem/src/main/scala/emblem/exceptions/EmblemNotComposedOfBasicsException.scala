package emblem.exceptions

import emblem.emblematic.Emblem
import emblem.emblematic.EmblemProp

/** an exception thrown when the user attempts to build an
 * [[emblem.emblematic.EmblematicPropPath]] where one of the intermediate steps in the
 * specified path is something that is not covered by the [[emblem.emblematic.Emblematic]]
 */
class EmblemNotComposedOfBasicsException[A](
  val emblem: Emblem[_],
  val prop: EmblemProp[_, _])
extends EmblemException(
  s"Emblem.basicPropPaths won't work for $emblem because it contains properties that are unions or collections")
