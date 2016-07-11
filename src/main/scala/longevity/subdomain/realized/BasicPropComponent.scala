package longevity.subdomain.realized

import emblem.TypeKey
import emblem.emblematic.basicTypes.basicTypeOrderings
import emblem.emblematic.EmblematicPropPath
import longevity.subdomain.persistent.Persistent

/** a component of a prop that is a basic type */
private[longevity] case class BasicPropComponent[P <: Persistent, A, B](
  innerPropPath: EmblematicPropPath[A, B],
  outerPropPath: EmblematicPropPath[P, B]) {

  val componentTypeKey: TypeKey[B] = innerPropPath.typeKey

  val ordering: Ordering[B] = basicTypeOrderings(componentTypeKey)

  val get: (A) => B = innerPropPath.get(_)

}
