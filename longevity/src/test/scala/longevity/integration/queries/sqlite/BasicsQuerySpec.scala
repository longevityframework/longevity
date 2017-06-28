package longevity.integration.queries.sqlite

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
import longevity.integration.queries.queryTestsExecutionContext
import scala.concurrent.Future

class BasicsQuerySpec extends QuerySpec[Future, DomainModel, Basics](
  new LongevityContext(TestLongevityConfigs.sqliteConfig))
    with ExerciseAkkaStreams[Future, DomainModel, Basics]
    with ExerciseFS2[Future, DomainModel, Basics]
    with ExerciseIterateeIo[Future, DomainModel, Basics]
    with ExercisePlayEnumerator[Future, DomainModel, Basics] {

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

  behavior of "SQLitePRepo.retrieveByQuery"

  it should "produce expected results for Query.FilterAll" in {
    exerciseQuery(Query(FilterAll()), true)
  }

  it should "produce expected results for simple equality queries" in {
    exerciseQuery(booleanProp eqs sample.boolean, true)
    exerciseQuery(charProp neq sample.char)
    exerciseQuery(dateTimeProp eqs sample.dateTime)
    exerciseQuery(doubleProp neq sample.double)
    exerciseQuery(floatProp eqs sample.float)
    exerciseQuery(intProp neq sample.int)
    exerciseQuery(longProp eqs sample.long)
    exerciseQuery(stringProp neq sample.string)

    // make sure Query.FilterAll() can occur inside greater expression
    exerciseQuery(stringProp neq sample.string and FilterAll())
  }

  behavior of "SQLitePRepo.retrieveByQuery"
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

  behavior of "SQLitePRepo.retrieveByQuery"
  it should "produce expected results for simple conditional queries" in {
    exerciseQuery(booleanProp lt sample.boolean and charProp lte sample.char, true)
    exerciseQuery(dateTimeProp gt sample.dateTime and doubleProp gte sample.double)
    exerciseQuery(floatProp lt sample.float or intProp lte sample.int)
    exerciseQuery(longProp gt sample.long or stringProp gte sample.string)
  }

  behavior of "SQLitePRepo.retrieveByQuery"
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
