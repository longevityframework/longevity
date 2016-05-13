package longevity.integration.queries

import longevity.test.QuerySpec
import longevity.integration.subdomain.allShorthands._
import scala.concurrent.ExecutionContext.Implicits.global

class AllShorthandsCassandraQuerySpec
extends QuerySpec[AllShorthands](cassandraContext, cassandraContext.testRepoPool) {

  lazy val sample = randomP

  val booleanProp = AllShorthands.prop[BooleanShorthand]("boolean")
  val charProp = AllShorthands.prop[CharShorthand]("char")
  val doubleProp = AllShorthands.prop[DoubleShorthand]("double")
  val floatProp = AllShorthands.prop[FloatShorthand]("float")
  val intProp = AllShorthands.prop[IntShorthand]("int")
  val longProp = AllShorthands.prop[LongShorthand]("long")
  val stringProp = AllShorthands.prop[StringShorthand]("string")
  val dateTimeProp = AllShorthands.prop[DateTimeShorthand]("dateTime")

  import AllShorthands.queryDsl._

  behavior of "CassandraRepo.retrieveByQuery"
  it should "produce expected results for simple equality queries" in {
    exerciseQuery(booleanProp eqs sample.boolean)
    exerciseQuery(charProp eqs sample.char)
    exerciseQuery(dateTimeProp eqs sample.dateTime)
    exerciseQuery(doubleProp eqs sample.double)
    exerciseQuery(floatProp eqs sample.float)
    exerciseQuery(intProp eqs sample.int)
    exerciseQuery(longProp eqs sample.long)
    exerciseQuery(stringProp eqs sample.string)
  }

  behavior of "CassandraRepo.retrieveByQuery"
  it should "produce expected results for simple conditional queries" in {
    exerciseQuery(floatProp eqs sample.float and booleanProp lt sample.boolean)
    exerciseQuery(floatProp eqs sample.float and charProp lte sample.char)
    exerciseQuery(floatProp eqs sample.float and dateTimeProp gt sample.dateTime)
    exerciseQuery(floatProp eqs sample.float and doubleProp gte sample.double)
    exerciseQuery(longProp eqs sample.long and floatProp lt sample.float)
    exerciseQuery(floatProp eqs sample.float and intProp lte sample.int)
    exerciseQuery(floatProp eqs sample.float and longProp gt sample.long)
    exerciseQuery(floatProp eqs sample.float and stringProp gte sample.string)
  }

  behavior of "CassandraRepo.retrieveByQuery"
  it should "produce expected results for nested conditional queries" in {
    exerciseQuery(
      booleanProp eqs sample.boolean and
      charProp lte sample.char and
      dateTimeProp lt sample.dateTime)
    exerciseQuery(
      dateTimeProp eqs sample.dateTime and (
        doubleProp gte sample.double and
        floatProp lt sample.float))
    exerciseQuery(
      floatProp eqs sample.float and
      intProp lte sample.int and
      longProp gt sample.long and
      stringProp gte sample.string)
  }

}
