package longevity

import emblem.TypeKeyMap

/** Provides support for constructing your domain.
  * 
  * Ideally, you wouldn't actually need to use any of the classes here. I.e.,
  * [[longevity.repo]] should not depend on anything here.
  * They should just be tools for the user to use if she chooses to.
  * But we'll see how that turns out.
  */
package object domain {

  /** A type key map of [[Entity]] to [[EntityType]]
   * @see emblem.TypeKeyMap */
  type EntityTypePool = TypeKeyMap[Entity, EntityType]

}
