package longevity

import emblem.TypeKeyMap

/** provides tools for constructing your subdomain */
package object subdomain {

  /** a type key map of [[Persistent]] to [[PType]]
   * @see emblem.TypeKeyMap
   */
  type PTypePool = TypeKeyMap[Persistent, PType]

  /** a type key map for [[EType component types]]
   * @see emblem.TypeKeyMap
   */
  type ETypePool = TypeKeyMap[Any, EType]

  /** an arbitrary [[KeyVal key value]] type for a given persistent type `P` */
  type AnyKeyVal[P <: Persistent] = KeyVal[P, V] forSome { type V <: KeyVal[P, V] }

}

