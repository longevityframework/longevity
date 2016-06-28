package longevity.subdomain.realized

import emblem.emblematic.EmblematicPropPath
import longevity.subdomain.persistent.Persistent
//import emblem.TypeKey

/** a component of a prop that is a basic type */
private[longevity] case class BasicPropComponent[P <: Persistent, A, B](
  innerPropPath: EmblematicPropPath[A, B],
  outerPropPath: EmblematicPropPath[P, B]) {

  val componentTypeKey = innerPropPath.typeKey
}

// ,
//  TODO
//   componentTypeKey: TypeKey[B],
//   get: (A) => B,
//   ordering: Ordering[A])
