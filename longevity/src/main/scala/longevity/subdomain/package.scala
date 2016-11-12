package longevity

import emblem.TypeKeyMap

/** provides tools for constructing your subdomain */
package object subdomain {

  /** a type key map for [[PType persistent types]]
   * @see emblem.TypeKeyMap
   */
  type PTypePool = TypeKeyMap[Any, PType]

  /** a type key map for [[EType component types]]
   * @see emblem.TypeKeyMap
   */
  type ETypePool = TypeKeyMap[Any, EType]

  /** an arbitrary [[KeyVal key value]] type for a given persistent type `P` */
  type AnyKeyVal[P] = KeyVal[P, V] forSome { type V <: KeyVal[P, V] }

}

