package longevity

import emblem.TypeKeyMap

/** provides tools for constructing your subdomain */
package object subdomain {

  /** a type key map of [[Embeddable]] to [[EType]]
   * @see emblem.TypeKeyMap
   */
  type ETypePool = TypeKeyMap[Embeddable, EType]

  /** an arbitrary [[KeyVal key value]] type for a given persistent type `P` */
  type AnyKeyVal[P <: Persistent] = KeyVal[P, V] forSome { type V <: KeyVal[P, V] }

}

