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

  /** an [[Assoc association]] to an unspecified type of [[RootEntity root]]. this is useful for building
   * stuff from `emblem.traversors` for traversing entities.
   * 
   * leaving this `private[longevity]` for now, but if any user-facing use-case comes up, we can expose it.
   */
  private[longevity] type AssocAny = Assoc[_ <: RootEntity]

}
