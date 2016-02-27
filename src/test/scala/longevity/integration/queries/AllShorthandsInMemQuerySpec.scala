package longevity.integration.queries

import longevity.subdomain.root.Query._
import longevity.test.QuerySpec
import longevity.integration.subdomain.allShorthands._

class AllShorthandsInMemQuerySpec
extends QuerySpec[AllShorthands](context.mongoContext, context.mongoContext.inMemTestRepoPool) {

  val repo = repoPool[AllShorthands]

  val booleanProp = AllShorthands.prop[BooleanShorthand]("boolean")
  val charProp = AllShorthands.prop[CharShorthand]("char")
  val doubleProp = AllShorthands.prop[DoubleShorthand]("double")
  val floatProp = AllShorthands.prop[FloatShorthand]("float")
  val intProp = AllShorthands.prop[IntShorthand]("int")
  val longProp = AllShorthands.prop[LongShorthand]("long")
  val stringProp = AllShorthands.prop[StringShorthand]("string")
  val dateTimeProp = AllShorthands.prop[DateTimeShorthand]("dateTime")

  behavior of "InMemRepo.retrieveByQuery"
  it should "produce expected results for shorthand equality queries" in {
    exerciseQTemplate(EqualityQTemplate(stringProp, EqOp))
    exerciseQTemplate(EqualityQTemplate(stringProp, NeqOp))
    exerciseQTemplate(EqualityQTemplate(dateTimeProp, EqOp))
    exerciseQTemplate(EqualityQTemplate(dateTimeProp, NeqOp))
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
      OrOp,
      EqualityQTemplate(longProp, EqOp)))
    exerciseQTemplate(ConditionalQTemplate(
      EqualityQTemplate(floatProp, EqOp),
      AndOp,
      EqualityQTemplate(longProp, EqOp)))
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
