package longevity.integration.queries.inmem

import longevity.TestLongevityConfigs
import longevity.context.LongevityContext
import longevity.integration.model.basics._
import longevity.model.query.FilterAll
import longevity.model.query.Query
import longevity.test.ExerciseAkkaStreams
import longevity.test.ExerciseFS2
import longevity.test.ExerciseIterateeIo
import longevity.test.ExercisePlayEnumerator
import longevity.test.QuerySpec
import longevity.integration.queries.queryTestsExecutionContext

class BasicsQuerySpec extends QuerySpec[Basics](
  new LongevityContext(domainModel, TestLongevityConfigs.inMemConfig))
    with ExerciseAkkaStreams[Basics]
    with ExerciseFS2[Basics]
    with ExerciseIterateeIo[Basics]
    with ExercisePlayEnumerator[Basics] {

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

  behavior of "InMemRepo.retrieveByQuery"

  it should "produce expected results for Query.FilterAll" in {
    exerciseQuery(Query(FilterAll()), true)
  }

  it should "produce expected results for simple equality queries" in {
    exerciseQuery(booleanProp eqs sample.boolean, true)
    exerciseQuery(booleanProp neq sample.boolean, true)
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

    // make sure Query.FilterAll() can occur inside greater expression
    exerciseQuery(stringProp neq sample.string and FilterAll())
  }

  behavior of "InMemRepo.retrieveByQuery"
  it should "produce expected results for simple ordering queries" in {
    exerciseQuery(booleanProp lt sample.boolean, true)
    exerciseQuery(charProp lte sample.char)
    exerciseQuery(dateTimeProp gt sample.dateTime)
    exerciseQuery(doubleProp gte sample.double)
    exerciseQuery(floatProp lt sample.float)
    exerciseQuery(intProp lte sample.int)
    exerciseQuery(longProp gt sample.long)
    exerciseQuery(stringProp gte sample.string)
  }

  behavior of "InMemRepo.retrieveByQuery"
  it should "produce expected results for simple conditional queries" in {
    exerciseQuery(booleanProp lt sample.boolean and charProp lte sample.char, true)
    exerciseQuery(dateTimeProp gt sample.dateTime and doubleProp gte sample.double)
    exerciseQuery(floatProp lt sample.float or intProp lte sample.int)
    exerciseQuery(longProp gt sample.long or stringProp gte sample.string)
  }

  behavior of "InMemRepo.retrieveByQuery"
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
