package longevity.integration.queries

import com.github.nscala_time.time.Imports._
import longevity.test.QuerySpec
import longevity.integration.subdomain.basics._
import longevity.exceptions.persistence.cassandra.AllInQueryException
import longevity.subdomain.ptype.Query.All
import longevity.subdomain.ptype.Query
import scala.concurrent.ExecutionContext.Implicits.global

class BasicsCassandraQuerySpec
extends QuerySpec[Basics](cassandraContext, cassandraContext.testRepoPool) {

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

  behavior of "CassandraRepo.retrieveByQuery"

  it should "produce expected results for Query.All" in {
    repo.retrieveByQuery(All()).failed.futureValue shouldBe a [AllInQueryException]
  }

  it should "produce expected results for simple equality queries" in {
    // only eqs here dur to cassandra query limitations
    exerciseQuery(booleanProp eqs sample.boolean, true)
    exerciseQuery(charProp eqs sample.char, true)
    exerciseQuery(dateTimeProp eqs sample.dateTime, true)
    exerciseQuery(doubleProp eqs sample.double, true)
    exerciseQuery(floatProp eqs sample.float, true)
    exerciseQuery(intProp eqs sample.int, true)
    exerciseQuery(longProp eqs sample.long, true)
    exerciseQuery(stringProp eqs sample.string, true)


    // make sure Query.All() can occur inside greater expression
    val query: Query[Basics] = stringProp eqs sample.string and All()
    repo.retrieveByQuery(query).failed.futureValue shouldBe a [AllInQueryException]
  }

  behavior of "CassandraRepo.retrieveByQuery"
  it should "produce expected results for simple conditional queries" in {
    exerciseQuery(floatProp eqs sample.float and booleanProp lt !sample.boolean, true)
    exerciseQuery(floatProp eqs sample.float and charProp lte sample.char, true)
    exerciseQuery(floatProp eqs sample.float and dateTimeProp gt sample.dateTime - 1.day, true)
    exerciseQuery(floatProp eqs sample.float and doubleProp gte sample.double, true)
    exerciseQuery(longProp eqs sample.long and floatProp lt sample.float + 2.0f, true)
    exerciseQuery(floatProp eqs sample.float and intProp lte sample.int, true)
    exerciseQuery(floatProp eqs sample.float and longProp gt sample.long - 1, true)
    exerciseQuery(floatProp eqs sample.float and stringProp gte sample.string, true)
  }

  behavior of "CassandraRepo.retrieveByQuery"
  it should "produce expected results for nested conditional queries" in {
    exerciseQuery(
      booleanProp eqs sample.boolean and
      charProp lte sample.char and
      dateTimeProp lt sample.dateTime + 1.hour,
      true)
    exerciseQuery(
      dateTimeProp eqs sample.dateTime and (
        doubleProp gte sample.double and
        floatProp lt sample.float + 7),
      true)
    exerciseQuery(
      floatProp eqs sample.float and
      intProp lte sample.int and
      longProp gt sample.long - 2 and
      stringProp gte sample.string,
      true)
  }

}
