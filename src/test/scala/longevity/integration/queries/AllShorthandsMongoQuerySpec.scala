package longevity.integration.queries

import longevity.test.QuerySpec
import longevity.integration.subdomain.allShorthands._
import scala.concurrent.ExecutionContext.Implicits.global

class AllShorthandsMongoQuerySpec extends QuerySpec[AllShorthands](
  context.mongoContext,
  context.mongoContext.testRepoPool) {

  val repo = repoPool[AllShorthands]
  lazy val sample = randomRoot

  val booleanProp = AllShorthands.prop[BooleanShorthand]("boolean")
  val charProp = AllShorthands.prop[CharShorthand]("char")
  val doubleProp = AllShorthands.prop[DoubleShorthand]("double")
  val floatProp = AllShorthands.prop[FloatShorthand]("float")
  val intProp = AllShorthands.prop[IntShorthand]("int")
  val longProp = AllShorthands.prop[LongShorthand]("long")
  val stringProp = AllShorthands.prop[StringShorthand]("string")
  val dateTimeProp = AllShorthands.prop[DateTimeShorthand]("dateTime")

  import AllShorthands.queryDsl._

  behavior of "InMemRepo.retrieveByQuery"
  it should "produce expected results for simple equality queries" in {
    exerciseQuery(booleanProp eqs sample.boolean)
    exerciseQuery(booleanProp neq sample.boolean)
    exerciseQuery(charProp eqs sample.char)
    exerciseQuery(charProp neq sample.char)
    exerciseQuery(dateTimeProp eqs sample.dateTime)
    exerciseQuery(dateTimeProp neq sample.dateTime)
    exerciseQuery(doubleProp eqs sample.double)
    exerciseQuery(doubleProp neq sample.double)
    exerciseQuery(floatProp eqs sample.float)
    exerciseQuery(floatProp neq sample.float)
    exerciseQuery(intProp eqs sample.int)
    exerciseQuery(intProp neq sample.int)
    exerciseQuery(longProp eqs sample.long)
    exerciseQuery(longProp neq sample.long)
    exerciseQuery(stringProp eqs sample.string)
    exerciseQuery(stringProp neq sample.string)
  }

  // behavior of "InMemRepo.retrieveByQuery"
  // it should "produce expected results for simple ordering queries" in {
  //   exerciseQuery(booleanProp lt sample.boolean)
  //   exerciseQuery(charProp lte sample.char)
  //   exerciseQuery(dateTimeProp gt sample.dateTime)
  //   exerciseQuery(doubleProp gte sample.double)
  //   exerciseQuery(floatProp lt sample.float)
  //   exerciseQuery(intProp lte sample.int)
  //   exerciseQuery(longProp gt sample.long)
  //   exerciseQuery(stringProp gte sample.string)
  // }

  behavior of "InMemRepo.retrieveByQuery"
  it should "produce expected results for simple conditional queries" in {
    exerciseQuery(booleanProp eqs sample.boolean and charProp neq sample.char)
    exerciseQuery(dateTimeProp eqs sample.dateTime and doubleProp neq sample.double)
    exerciseQuery(floatProp eqs sample.float or intProp neq sample.int)
    exerciseQuery(longProp eqs sample.long or stringProp neq sample.string)
  }

  // behavior of "InMemRepo.retrieveByQuery"
  // it should "produce expected results for simple conditional queries" in {
  //   exerciseQuery(booleanProp lt sample.boolean and charProp lte sample.char)
  //   exerciseQuery(dateTimeProp gt sample.dateTime and doubleProp gte sample.double)
  //   exerciseQuery(floatProp lt sample.float or intProp lte sample.int)
  //   exerciseQuery(longProp gt sample.long or stringProp gte sample.string)
  // }

  behavior of "InMemRepo.retrieveByQuery"
  it should "produce expected results for nested conditional queries" in {
    exerciseQuery(
      booleanProp eqs sample.boolean and
      charProp eqs sample.char and
      dateTimeProp neq sample.dateTime)
    exerciseQuery(
      dateTimeProp eqs sample.dateTime or (
        doubleProp eqs sample.double or floatProp neq sample.float))
    exerciseQuery(
      floatProp eqs sample.float or
      intProp eqs sample.int or
      longProp eqs sample.long or
      stringProp neq sample.string)
  }

  // behavior of "InMemRepo.retrieveByQuery"
  // it should "produce expected results for nested conditional queries" in {
  //   exerciseQuery(
  //     booleanProp lt sample.boolean and
  //     charProp lte sample.char and
  //     dateTimeProp neq sample.dateTime)
  //   exerciseQuery(
  //     dateTimeProp gt sample.dateTime or (
  //       doubleProp gte sample.double or floatProp lt sample.float))
  //   exerciseQuery(
  //     floatProp lt sample.float or
  //     intProp lte sample.int or
  //     longProp gt sample.long or
  //     stringProp gte sample.string)
  // }

}
