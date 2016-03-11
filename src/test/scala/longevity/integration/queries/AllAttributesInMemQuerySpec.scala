package longevity.integration.queries

import com.github.nscala_time.time.Imports._
import longevity.test.QuerySpec
import longevity.integration.subdomain.allAttributes._
import scala.concurrent.ExecutionContext.Implicits.global

class AllAttributesInMemQuerySpec
extends QuerySpec[AllAttributes](context.mongoContext, context.mongoContext.inMemTestRepoPool) {

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

  behavior of "InMemRepo.retrieveByQuery"
  it should "produce expected results for simple ordering queries" in {
    exerciseQuery(booleanProp lt sample.boolean)
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
    exerciseQuery(booleanProp lt sample.boolean and charProp lte sample.char)
    exerciseQuery(dateTimeProp gt sample.dateTime and doubleProp gte sample.double)
    exerciseQuery(floatProp lt sample.float or intProp lte sample.int)
    exerciseQuery(longProp gt sample.long or stringProp gte sample.string)
  }

  behavior of "InMemRepo.retrieveByQuery"
  it should "produce expected results for nested conditional queries" in {
    exerciseQuery(
      booleanProp lt sample.boolean and
      charProp lte sample.char and
      dateTimeProp neq sample.dateTime)
    exerciseQuery(
      dateTimeProp gt sample.dateTime or (
        doubleProp gte sample.double or floatProp lt sample.float))
    exerciseQuery(
      floatProp lt sample.float or
      intProp lte sample.int or
      longProp gt sample.long or
      stringProp gte sample.string)
  }

}
