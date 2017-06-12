package longevity.model.realized

import typekey.TypeKey
import longevity.emblem.emblematic.basicTypes.basicTypeOrderings
import longevity.emblem.emblematic.EmblematicPropPath

/** a component of a realized property that is a basic type */
private[longevity] case class RealizedPropComponent[P, A, B](
  innerPropPath: EmblematicPropPath[A, B],
  outerPropPath: EmblematicPropPath[P, B]) {

  def inlinedPath = outerPropPath.inlinedPath

  val componentTypeKey: TypeKey[B] = innerPropPath.typeKey

  val ordering: Ordering[B] = basicTypeOrderings(componentTypeKey)

  val get: (A) => B = innerPropPath.get(_)

}
