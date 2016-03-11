package longevity

import emblem.imports._
import emblem.ExtractorFor
import emblem.TypeBoundFunction

/** provides support for constructing your subdomain */
package object subdomain {

  // pools:

  /** a shorthand with the abbreviated type unspecified. this type is equivalent
   * to `Shorthand[Actual, _]`, except with a single type parameter `Actual`.
   * this allows it to be used as a key or value in a `TypeBoundMap` or
   * `TypeKeyMap`.
   */
  type ShorthandFor[Actual] = Shorthand[Actual, _]

  /** an `emblem.TypeKeyMap` of [[Shorthand shorthands]], indexed by the `Actual` type */
  type ShorthandPool = TypeKeyMap[Any, ShorthandFor]

  private[longevity] def shorthandPoolToExtractorPool(shorthandPool: ShorthandPool): ExtractorPool = {
    val shorthandToExtractor = new TypeBoundFunction[Any, ShorthandFor, ExtractorFor] {
      def apply[TypeParam](shorthand: ShorthandFor[TypeParam]): ExtractorFor[TypeParam] =
        shorthand.extractor
    }
    shorthandPool.mapValues(shorthandToExtractor)
  }

  /** a type key map of [[Entity]] to [[EntityType]]
   * @see emblem.TypeKeyMap
   */
  type EntityTypePool = TypeKeyMap[Entity, EntityType]

  /** a type key map of [[Persistent]] to [[PType]]
   * @see emblem.TypeKeyMap
   */
  type PTypePool = TypeKeyMap[Persistent, PType]

  /** an [[Assoc association]] to an unspecified type of [[Persistent persistent
   * entity]]. this is useful for building stuff from `emblem.traversors` for
   * traversing entities.
   * 
   * leaving this `private[longevity]` for now, but if any user-facing use-case
   * comes up, we can expose it.
   */
  private[longevity] type AssocAny = Assoc[_ <: Persistent]

  // synonyms:

  /** a core domain. functionally equivalent to a [[Subdomain]] */
  type CoreDomain = Subdomain

  /** a supporting subdomain. functionally equivalent to a [[Subdomain]] */
  type SupportingSubdomain = Subdomain

  /** a generic subdomain. functionally equivalent to a [[Subdomain]] */
  type GenericSubdomain = Subdomain

}
