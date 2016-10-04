package longevity.subdomain

import emblem.TypeKeyMap

/** provides tools for defining the types for your persistent classes */
package object ptype {

  /** a type key map of [[longevity.subdomain.persistent.Persistent
   * Persistent]] to [[PType]]
   * @see emblem.TypeKeyMap
   */
  type PTypePool = TypeKeyMap[Persistent, PType]

  /** an arbitrary [[Key key]] type for a given persistent type `P` */
  type AnyKey[P <: Persistent] = Key[P, V] forSome { type V <: KeyVal[P, V] }

}
