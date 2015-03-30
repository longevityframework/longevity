package longevity

import emblem._

/** the context package contains the [[LongevityContext]] and support classes. at the moment, all things
 * [[Shorthand]] are living here, as they are part of the context, and don't have their own place yet. it might
 * make sense to create a `shorthand` package.
 */
package object context {

  /** a shorthand with the abbreviated type unspecified. this type is equivalent to `Shorthand[Actual, _]`,
   * except with a single type parameter `Actual`. this allows it to be used as a key or value in a
   * `TypeBoundMap` or `TypeKeyMap`
   */
  type ShorthandFor[Actual] = Shorthand[Actual, _]

  /** A [[TypeKeyMap]] of `Actual` to [[Shorthand]] */
  type ShorthandPool = TypeKeyMap[Any, ShorthandFor]

  private[longevity] def shorthandPoolToExtractorPool(shorthandPool: ShorthandPool): ExtractorPool = {
    val shorthandToExtractor = new TypeBoundFunction[Any, ShorthandFor, ExtractorFor] {
      def apply[TypeParam](shorthand: ShorthandFor[TypeParam]): ExtractorFor[TypeParam] = shorthand.extractor
    }
    shorthandPool.mapValues(shorthandToExtractor)
  }

}
