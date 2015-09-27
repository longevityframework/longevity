package longevity.integration

// TODO integration test with a shorthand for a nat key
// TODO integration test with an assoc in the nat key
// TODO integration test with composite nat key


import com.github.nscala_time.time.Imports._
import longevity.context._
import longevity.subdomain._

/** covers everything found in the rest of the integration tests */
package object master {

  val booleanShorthand = Shorthand[BooleanShorthand, Boolean]
  val charShorthand = Shorthand[CharShorthand, Char]
  val dateTimeShorthand = Shorthand[DateTimeShorthand, DateTime]
  val doubleShorthand = Shorthand[DoubleShorthand, Double]
  val floatShorthand = Shorthand[FloatShorthand, Float]
  val intShorthand = Shorthand[IntShorthand, Int]
  val longShorthand = Shorthand[LongShorthand, Long]
  val stringShorthand = Shorthand[StringShorthand, String]

  implicit val shorthandPool = ShorthandPool.empty +
    booleanShorthand +
    charShorthand +
    dateTimeShorthand +
    doubleShorthand +
    floatShorthand +
    intShorthand +
    longShorthand +
    stringShorthand

  object context {

    val entityTypes = EntityTypePool(
      AllAttributes,
      AllShorthands,
      Associated,
      AttributeLists,
      AttributeOptions,
      AttributeSets,
      Component,
      ComponentWithAssoc,
      ComponentWithShorthands,
      OneAttribute,
      OneShorthand,
      WithAssoc,
      WithAssocList,
      WithAssocOption,
      WithAssocSet,
      WithComponent,
      WithComponentList,
      WithComponentOption,
      WithComponentSet,
      WithComponentWithAssoc,
      WithComponentWithShorthands)

    val subdomain = Subdomain("Master", entityTypes, shorthandPool)
    val longevityContext = LongevityContext(subdomain, Mongo)
  }

}
