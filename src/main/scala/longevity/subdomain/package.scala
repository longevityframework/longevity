package longevity

import emblem.TypeKeyMap
import longevity.subdomain.persistent.Persistent

/** provides support for constructing your subdomain */
package object subdomain {

  /** a core domain. functionally equivalent to a [[Subdomain]] */
  type CoreDomain = Subdomain

  /** a supporting subdomain. functionally equivalent to a [[Subdomain]] */
  type SupportingSubdomain = Subdomain

  /** a generic subdomain. functionally equivalent to a [[Subdomain]] */
  type GenericSubdomain = Subdomain

  /** a shorthand with the abbreviated type unspecified. this type is equivalent
   * to `Shorthand[Actual, _]`, except with a single type parameter `Actual`.
   * this allows it to be used as a key or value in a `TypeBoundMap` or
   * `TypeKeyMap`.
   */
  type ShorthandFor[Actual] = Shorthand[Actual, _]

  /** an `emblem.TypeKeyMap` of [[Shorthand shorthands]], indexed by the `Actual` type */
  type ShorthandPool = TypeKeyMap[Any, ShorthandFor]

  /** an [[Assoc association]] to an unspecified type of [[Persistent persistent
   * entity]]. this is useful for building stuff from `emblem.traversors` for
   * traversing entities.
   * 
   * leaving this `private[longevity]` for now, but if any user-facing use-case
   * comes up, we can expose it.
   */
  private[longevity] type AssocAny = Assoc[_ <: Persistent]

}
