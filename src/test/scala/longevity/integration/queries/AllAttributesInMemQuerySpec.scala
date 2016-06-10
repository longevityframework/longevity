package longevity.integration.queries

import com.github.nscala_time.time.Imports._
import longevity.test.QuerySpec
import longevity.integration.subdomain.allAttributes._
import longevity.subdomain.ptype.Query.All
import scala.concurrent.ExecutionContext.Implicits.global

class AllAttributesInMemQuerySpec
extends QuerySpec[AllAttributes](mongoContext, mongoContext.inMemTestRepoPool) {

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

  behavior of "InMemRepo.retrieveByQuery"

  it should "produce expected results for Query.All" in {
    exerciseQuery(All(), true)
  }

  it should "produce expected results for simple equality queries" in {
    exerciseQuery(booleanProp eqs sample.boolean, true)
    exerciseQuery(booleanProp neq sample.boolean, true)
    exerciseQuery(charProp eqs sample.char, true)
    exerciseQuery(charProp neq sample.char, true)
    exerciseQuery(dateTimeProp eqs sample.dateTime, true)
    exerciseQuery(dateTimeProp neq sample.dateTime, true)
    exerciseQuery(doubleProp eqs sample.double, true)
    exerciseQuery(doubleProp neq sample.double, true)
    exerciseQuery(floatProp eqs sample.float, true)
    exerciseQuery(floatProp neq sample.float, true)
    exerciseQuery(intProp eqs sample.int, true)
    exerciseQuery(intProp neq sample.int, true)
    exerciseQuery(longProp eqs sample.long, true)
    exerciseQuery(longProp neq sample.long, true)
    exerciseQuery(stringProp eqs sample.string, true)
    exerciseQuery(stringProp neq sample.string, true)

    // make sure Query.All() can occur inside greater expression
    exerciseQuery(stringProp neq sample.string and All(), true)
  }

  behavior of "InMemRepo.retrieveByQuery"
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

  behavior of "InMemRepo.retrieveByQuery"
  it should "produce expected results for simple conditional queries" in {
    exerciseQuery(booleanProp lt sample.boolean and charProp lte sample.char, true)
    exerciseQuery(dateTimeProp gt sample.dateTime and doubleProp gte sample.double, true)
    exerciseQuery(floatProp lt sample.float or intProp lte sample.int, true)
    exerciseQuery(longProp gt sample.long or stringProp gte sample.string, true)
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
