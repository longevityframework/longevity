package longevity.integration.queries

import com.github.nscala_time.time.Imports._
import longevity.test.QuerySpec
import longevity.integration.subdomain.allAttributes._
import scala.concurrent.ExecutionContext.Implicits.global

class AllAttributesCassandraQuerySpec
extends QuerySpec[AllAttributes](cassandraContext, cassandraContext.testRepoPool) {

  lazy val sample = randomP

  val booleanProp = AllAttributes.prop[Boolean]("boolean")
  val charProp = AllAttributes.prop[Char]("char")
  val doubleProp = AllAttributes.prop[Double]("double")
  val floatProp = AllAttributes.prop[Float]("float")
  val intProp = AllAttributes.prop[Int]("int")
  val longProp = AllAttributes.prop[Long]("long")
  val stringProp = AllAttributes.prop[String]("string")
  val dateTimeProp = AllAttributes.prop[DateTime]("dateTime")

  import AllAttributes.queryDsl._

  behavior of "CassandraRepo.retrieveByQuery"
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
