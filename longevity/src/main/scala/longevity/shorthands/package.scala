package longevity

import emblem.imports._
import emblem.ExtractorFor
import emblem.TypeBoundFunction

package object shorthands {

  /** a shorthand with the abbreviated type unspecified. this type is equivalent to `Shorthand[Actual, _]`,
   * except with a single type parameter `Actual`. this allows it to be used as a key or value in a
   * `TypeBoundMap` or `TypeKeyMap`.
   */
  type ShorthandFor[Actual] = Shorthand[Actual, _]

  /** an `emblem.TypeKeyMap` of [[Shorthand shorthands]], indexed by the `Actual` type */
  type ShorthandPool = TypeKeyMap[Any, ShorthandFor]

  private[longevity] def shorthandPoolToExtractorPool(shorthandPool: ShorthandPool): ExtractorPool = {
    val shorthandToExtractor = new TypeBoundFunction[Any, ShorthandFor, ExtractorFor] {
      def apply[TypeParam](shorthand: ShorthandFor[TypeParam]): ExtractorFor[TypeParam] = shorthand.extractor
    }
    shorthandPool.mapValues(shorthandToExtractor)
  }

}
