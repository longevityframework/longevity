package longevity.subdomain.realized

import emblem.TypeKey
import emblem.emblematic.basicTypes.basicTypeOrderings
import emblem.emblematic.EmblematicPropPath
import longevity.subdomain.Persistent

/** a component of a realized property that is a basic type */
private[longevity] case class RealizedPropComponent[P <: Persistent, A, B](
  innerPropPath: EmblematicPropPath[A, B],
  outerPropPath: EmblematicPropPath[P, B]) {

  val componentTypeKey: TypeKey[B] = innerPropPath.typeKey

  val ordering: Ordering[B] = basicTypeOrderings(componentTypeKey)

  val get: (A) => B = innerPropPath.get(_)

}
