package longevity.integration.queries

import longevity.test.QuerySpec
import longevity.integration.subdomain.allShorthands._

class AllShorthandsInMemQuerySpec
extends QuerySpec[AllShorthands](mongoContext, mongoContext.inMemTestRepoPool) {

  val repo = repoPool[AllShorthands]

  val booleanProp = AllShorthands.prop[BooleanShorthand]("boolean")
  val charProp = AllShorthands.prop[CharShorthand]("char")
  val doubleProp = AllShorthands.prop[DoubleShorthand]("double")
  val floatProp = AllShorthands.prop[FloatShorthand]("float")
  val intProp = AllShorthands.prop[IntShorthand]("int")
  val longProp = AllShorthands.prop[LongShorthand]("long")
  val stringProp = AllShorthands.prop[StringShorthand]("string")
  val dateTimeProp = AllShorthands.prop[DateTimeShorthand]("dateTime")

  behavior of "InMemRepo.retrieveByQuery"
  it should "produce expected results for shorthand equality queries" in {
    exerciseQTemplate(EqualityQTemplate(stringProp))
    exerciseQTemplate(EqualityQTemplate(dateTimeProp))
  }

  behavior of "InMemRepo.retrieveByQuery"
  it should "produce expected results for simple conditional queries" in {
    exerciseQTemplate(ConditionalQTemplate(EqualityQTemplate(floatProp), EqualityQTemplate(longProp)))
  }

  behavior of "InMemRepo.retrieveByQuery"
  it should "produce expected results for nested conditional queries" in {
    exerciseQTemplate(
      ConditionalQTemplate(
        ConditionalQTemplate(EqualityQTemplate(floatProp), EqualityQTemplate(longProp)),
        EqualityQTemplate(dateTimeProp)))
    exerciseQTemplate(
      ConditionalQTemplate(
        EqualityQTemplate(dateTimeProp),
        ConditionalQTemplate(EqualityQTemplate(floatProp), EqualityQTemplate(longProp))))
  }

}
