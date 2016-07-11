package longevity.integration.queries

import com.github.nscala_time.time.Imports._
import longevity.test.QuerySpec
import longevity.integration.subdomain.basics._
import longevity.subdomain.ptype.Query.All
import scala.concurrent.ExecutionContext.Implicits.global

class BasicsMongoQuerySpec
extends QuerySpec[Basics](mongoContext, mongoContext.testRepoPool) {

  lazy val sample = randomP

  val booleanProp = Basics.prop[Boolean]("boolean")
  val charProp = Basics.prop[Char]("char")
  val doubleProp = Basics.prop[Double]("double")
  val floatProp = Basics.prop[Float]("float")
  val intProp = Basics.prop[Int]("int")
  val longProp = Basics.prop[Long]("long")
  val stringProp = Basics.prop[String]("string")
  val dateTimeProp = Basics.prop[DateTime]("dateTime")

  import Basics.queryDsl._

  behavior of "MongoRepo.retrieveByQuery"

  it should "produce expected results for Query.All" in {
    exerciseQuery(All(), true)
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

    // make sure Query.All() can occur inside greater expression
    exerciseQuery(stringProp neq sample.string and All(), true)
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
