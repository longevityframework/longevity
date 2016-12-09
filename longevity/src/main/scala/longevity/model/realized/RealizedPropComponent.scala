package longevity.model.realized

import emblem.TypeKey
import emblem.emblematic.basicTypes.basicTypeOrderings
import emblem.emblematic.EmblematicPropPath

/** a component of a realized property that is a basic type */
private[longevity] case class RealizedPropComponent[P, A, B](
  innerPropPath: EmblematicPropPath[A, B],
  outerPropPath: EmblematicPropPath[P, B]) {

  def inlinedPath = outerPropPath.inlinedPath

  val componentTypeKey: TypeKey[B] = innerPropPath.typeKey

  val ordering: Ordering[B] = basicTypeOrderings(componentTypeKey)

  val get: (A) => B = innerPropPath.get(_)

}
