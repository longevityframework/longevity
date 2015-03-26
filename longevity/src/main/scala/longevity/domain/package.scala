package longevity

import emblem.TypeKeyMap

/** provides support for constructing your domain */
package object domain {

  /** a type key map of [[Entity]] to [[EntityType]]
   * @see emblem.TypeKeyMap */
  type EntityTypePool = TypeKeyMap[Entity, EntityType]

  /** a type key map of [[RootEntity]] to [[RootEntityType]]
   * @see emblem.TypeKeyMap */
  type RootEntityTypePool = TypeKeyMap[RootEntity, RootEntityType]

  /** TODO scaladoc
   * TODO expose if use-case
   */
  private[longevity] type AssocAny = Assoc[_ <: RootEntity]

}
