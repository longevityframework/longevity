package longevity.integration.queries

import com.github.nscala_time.time.Imports._
import longevity.test.QuerySpec
import longevity.integration.subdomain.allAttributes._

class AllAttributesInMemQuerySpec
extends QuerySpec[AllAttributes](context.mongoContext, context.mongoContext.inMemTestRepoPool) {

  val repo = repoPool[AllAttributes]

  val booleanProp = AllAttributes.prop[Boolean]("boolean")
  val charProp = AllAttributes.prop[Char]("char")
  val doubleProp = AllAttributes.prop[Double]("double")
  val floatProp = AllAttributes.prop[Float]("float")
  val intProp = AllAttributes.prop[Int]("int")
  val longProp = AllAttributes.prop[Long]("long")
  val stringProp = AllAttributes.prop[String]("string")
  val dateTimeProp = AllAttributes.prop[DateTime]("dateTime")

  behavior of "InMemRepo.retrieveByQuery"
  it should "produce expected results for simple equality queries" in {
    exerciseQTemplate(EqualityQTemplate(booleanProp))
    exerciseQTemplate(EqualityQTemplate(charProp))
    exerciseQTemplate(EqualityQTemplate(doubleProp))
    exerciseQTemplate(EqualityQTemplate(floatProp))
    exerciseQTemplate(EqualityQTemplate(intProp))
    exerciseQTemplate(EqualityQTemplate(longProp))
    exerciseQTemplate(EqualityQTemplate(stringProp))
    exerciseQTemplate(EqualityQTemplate(dateTimeProp))
  }

  behavior of "InMemRepo.retrieveByQuery"
  it should "produce expected results for simple ordering queries" in {
    exerciseQTemplate(OrderingQTemplate(booleanProp))
    exerciseQTemplate(OrderingQTemplate(charProp))
    exerciseQTemplate(OrderingQTemplate(doubleProp))
    exerciseQTemplate(OrderingQTemplate(floatProp))
    exerciseQTemplate(OrderingQTemplate(intProp))
    exerciseQTemplate(OrderingQTemplate(longProp))
    exerciseQTemplate(OrderingQTemplate(stringProp))
    exerciseQTemplate(OrderingQTemplate(dateTimeProp))
  }

  behavior of "InMemRepo.retrieveByQuery"
  it should "produce expected results for simple conditional queries" in {
    exerciseQTemplate(ConditionalQTemplate(EqualityQTemplate(floatProp), EqualityQTemplate(longProp)))
    exerciseQTemplate(ConditionalQTemplate(EqualityQTemplate(floatProp), OrderingQTemplate(longProp)))
    exerciseQTemplate(ConditionalQTemplate(OrderingQTemplate(floatProp), EqualityQTemplate(longProp)))
    exerciseQTemplate(ConditionalQTemplate(OrderingQTemplate(floatProp), OrderingQTemplate(longProp)))
  }

  behavior of "InMemRepo.retrieveByQuery"
  it should "produce expected results for nested conditional queries" in {
    exerciseQTemplate(
      ConditionalQTemplate(
        ConditionalQTemplate(EqualityQTemplate(floatProp), EqualityQTemplate(longProp)),
        EqualityQTemplate(dateTimeProp)))
    exerciseQTemplate(
      ConditionalQTemplate(
        ConditionalQTemplate(OrderingQTemplate(floatProp), OrderingQTemplate(longProp)),
        OrderingQTemplate(dateTimeProp)))
    exerciseQTemplate(
      ConditionalQTemplate(
        EqualityQTemplate(dateTimeProp),
        ConditionalQTemplate(EqualityQTemplate(floatProp), EqualityQTemplate(longProp))))
  }

}
