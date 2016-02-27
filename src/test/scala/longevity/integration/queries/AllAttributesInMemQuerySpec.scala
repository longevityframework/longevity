package longevity.integration.queries

import com.github.nscala_time.time.Imports._
import longevity.subdomain.root.Query._
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
    exerciseQTemplate(EqualityQTemplate(booleanProp, EqOp))
    exerciseQTemplate(EqualityQTemplate(booleanProp, NeqOp))
    exerciseQTemplate(EqualityQTemplate(charProp, EqOp))
    exerciseQTemplate(EqualityQTemplate(doubleProp, NeqOp))
    exerciseQTemplate(EqualityQTemplate(floatProp, EqOp))
    exerciseQTemplate(EqualityQTemplate(intProp, NeqOp))
    exerciseQTemplate(EqualityQTemplate(longProp, EqOp))
    exerciseQTemplate(EqualityQTemplate(stringProp, NeqOp))
    exerciseQTemplate(EqualityQTemplate(dateTimeProp, EqOp))
  }

  behavior of "InMemRepo.retrieveByQuery"
  it should "produce expected results for simple ordering queries" in {
    exerciseQTemplate(OrderingQTemplate(booleanProp, LtOp))
    exerciseQTemplate(OrderingQTemplate(charProp, LteOp))
    exerciseQTemplate(OrderingQTemplate(doubleProp, GtOp))
    exerciseQTemplate(OrderingQTemplate(floatProp, GteOp))
    exerciseQTemplate(OrderingQTemplate(intProp, LtOp))
    exerciseQTemplate(OrderingQTemplate(longProp, LteOp))
    exerciseQTemplate(OrderingQTemplate(stringProp, GtOp))
    exerciseQTemplate(OrderingQTemplate(dateTimeProp, GteOp))
  }

  behavior of "InMemRepo.retrieveByQuery"
  it should "produce expected results for simple conditional queries" in {
    exerciseQTemplate(ConditionalQTemplate(
      EqualityQTemplate(floatProp, EqOp),
      AndOp,
      EqualityQTemplate(longProp, NeqOp)))
    exerciseQTemplate(ConditionalQTemplate(
      EqualityQTemplate(floatProp, EqOp),
      AndOp,
      OrderingQTemplate(longProp, LtOp)))
    exerciseQTemplate(ConditionalQTemplate(
      OrderingQTemplate(floatProp, GteOp),
      OrOp,
      EqualityQTemplate(longProp, EqOp)))
    exerciseQTemplate(ConditionalQTemplate(
      OrderingQTemplate(floatProp, LteOp),
      OrOp,
      OrderingQTemplate(longProp, GtOp)))
  }

  behavior of "InMemRepo.retrieveByQuery"
  it should "produce expected results for nested conditional queries" in {
    exerciseQTemplate(
      ConditionalQTemplate(
        ConditionalQTemplate(
          EqualityQTemplate(floatProp, EqOp),
          AndOp,
          EqualityQTemplate(longProp, EqOp)),
        AndOp,
        EqualityQTemplate(dateTimeProp, NeqOp)))
    exerciseQTemplate(
      ConditionalQTemplate(
        ConditionalQTemplate(
          OrderingQTemplate(floatProp, GtOp),
          OrOp,
          OrderingQTemplate(longProp, LtOp)),
        OrOp,
        OrderingQTemplate(dateTimeProp, LtOp)))
    exerciseQTemplate(
      ConditionalQTemplate(
        EqualityQTemplate(dateTimeProp, EqOp),
        OrOp,
        ConditionalQTemplate(
          EqualityQTemplate(floatProp, EqOp),
          OrOp,
          EqualityQTemplate(longProp, EqOp))))
  }

}
