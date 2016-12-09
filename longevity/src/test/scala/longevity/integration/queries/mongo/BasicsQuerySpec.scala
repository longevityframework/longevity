package longevity.integration.queries.mongo

import longevity.TestLongevityConfigs
import longevity.context.LongevityContext
import longevity.test.QuerySpec
import longevity.integration.subdomain.basics._
import longevity.model.query.Query
import longevity.model.query.FilterAll
import scala.concurrent.ExecutionContext.Implicits.global

class BasicsQuerySpec extends QuerySpec[Basics](
  new LongevityContext(subdomain, TestLongevityConfigs.mongoConfig)) {

  lazy val sample = randomP

  val booleanProp = Basics.props.boolean
  val charProp = Basics.props.char
  val doubleProp = Basics.props.double
  val floatProp = Basics.props.float
  val intProp = Basics.props.int
  val longProp = Basics.props.long
  val stringProp = Basics.props.string
  val dateTimeProp = Basics.props.dateTime

  import Basics.queryDsl._

  behavior of "MongoRepo.retrieveByQuery"

  it should "produce expected results for Query.FilterAll" in {
    exerciseQuery(Query(FilterAll()), true)
  }

  it should "produce expected results for simple equality queries" in {
    exerciseQuery(booleanProp eqs sample.boolean, true)
    exerciseQuery(charProp neq sample.char, true)
    exerciseQuery(dateTimeProp eqs sample.dateTime, true)
    exerciseQuery(doubleProp neq sample.double, true)
    exerciseQuery(floatProp eqs sample.float, true)
    exerciseQuery(intProp neq sample.int, true)
    exerciseQuery(longProp eqs sample.long, true)
    exerciseQuery(stringProp neq sample.string, true)

    // make sure Query.FilterAll() can occur inside greater expression
    exerciseQuery(stringProp neq sample.string and FilterAll(), true)
  }

  behavior of "MongoRepo.retrieveByQuery"
  it should "produce expected results for simple ordering queries" in {
    exerciseQuery(booleanProp lt sample.boolean, true)
    exerciseQuery(charProp lte sample.char, true)
    exerciseQuery(dateTimeProp gt sample.dateTime, true)
    exerciseQuery(doubleProp gte sample.double, true)
    exerciseQuery(floatProp lt sample.float, true)
    exerciseQuery(intProp lte sample.int, true)
    exerciseQuery(longProp gt sample.long, true)
    exerciseQuery(stringProp gte sample.string, true)
  }

  behavior of "MongoRepo.retrieveByQuery"
  it should "produce expected results for simple conditional queries" in {
    exerciseQuery(booleanProp lt sample.boolean and charProp lte sample.char, true)
    exerciseQuery(dateTimeProp gt sample.dateTime and doubleProp gte sample.double, true)
    exerciseQuery(floatProp lt sample.float or intProp lte sample.int, true)
    exerciseQuery(longProp gt sample.long or stringProp gte sample.string, true)
  }

  behavior of "MongoRepo.retrieveByQuery"
  it should "produce expected results for nested conditional queries" in {
    exerciseQuery(
      booleanProp lt sample.boolean and
      charProp lte sample.char and
      dateTimeProp neq sample.dateTime,
      true)
    exerciseQuery(
      dateTimeProp gt sample.dateTime or (
        doubleProp gte sample.double or floatProp lt sample.float),
      true)
    exerciseQuery(
      floatProp lt sample.float or
      intProp lte sample.int or
      longProp gt sample.long or
      stringProp gte sample.string,
      true)
  }

}
