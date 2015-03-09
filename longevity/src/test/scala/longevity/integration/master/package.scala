package longevity.integration

import emblem._
import longevity.domain._

/** covers everything found in the rest of the integration tests */
package object master {

  val entityTypes = EntityTypePool() +
    AllAttributes +
    Associated +
    OneAttribute +
    OneShorthand +
    WithAssoc

  val booleanShorthand = shorthandFor[BooleanShorthand, Boolean]
  val charShorthand = shorthandFor[CharShorthand, Char]
  val doubleShorthand = shorthandFor[DoubleShorthand, Double]
  val floatShorthand = shorthandFor[FloatShorthand, Float]
  val intShorthand = shorthandFor[IntShorthand, Int]
  val longShorthand = shorthandFor[LongShorthand, Long]
  val stringShorthand = shorthandFor[StringShorthand, String]

  val shorthandPool = ShorthandPool() +
    booleanShorthand +
    charShorthand +
    doubleShorthand +
    floatShorthand +
    intShorthand +
    longShorthand +
    stringShorthand

  val boundedContext = BoundedContext("Master", entityTypes, shorthandPool)

  val inMemRepoPool = longevity.repo.inMemRepoPool(boundedContext)

  val mongoRepoPool = longevity.repo.mongoRepoPool(boundedContext)

}
