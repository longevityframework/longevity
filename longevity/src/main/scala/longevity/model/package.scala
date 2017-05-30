package longevity

import emblem.TypeKeyMap

/** provides tools for constructing your domain model */
package object model {

  /** a type key map for [[CType component types]]
   * @see emblem.TypeKeyMap
   */
  type CTypePool = TypeKeyMap[Any, CType]

}

