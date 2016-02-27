package longevity.integration.queries

import com.github.nscala_time.time.Imports._
import longevity.subdomain.root.Query._
import longevity.test.QuerySpec
import longevity.integration.subdomain.allAttributes._

class AllAttributesCassandraQuerySpec
extends QuerySpec[AllAttributes](context.cassandraContext, context.cassandraContext.testRepoPool) {

  val repo = repoPool[AllAttributes]

  val booleanProp = AllAttributes.prop[Boolean]("boolean")
  val charProp = AllAttributes.prop[Char]("char")
  val doubleProp = AllAttributes.prop[Double]("double")
  val floatProp = AllAttributes.prop[Float]("float")
  val intProp = AllAttributes.prop[Int]("int")
  val longProp = AllAttributes.prop[Long]("long")
  val stringProp = AllAttributes.prop[String]("string")
  val dateTimeProp = AllAttributes.prop[DateTime]("dateTime")

  behavior of "CassandraRepo.retrieveByQuery"
  it should "produce expected results for simple equality queries" in {
    exerciseQTemplate(EqualityQTemplate(booleanProp, EqOp))
    exerciseQTemplate(EqualityQTemplate(charProp, EqOp))
    exerciseQTemplate(EqualityQTemplate(floatProp, EqOp))
    exerciseQTemplate(EqualityQTemplate(longProp, EqOp))
    exerciseQTemplate(EqualityQTemplate(dateTimeProp, EqOp))
  }

  // behavior of "CassandraRepo.retrieveByQuery"
  // it should "produce expected results for simple ordering queries" in {
  //   exerciseQTemplate(OrderingQTemplate(booleanProp, LtOp))
  //   exerciseQTemplate(OrderingQTemplate(charProp, LteOp))
  //   exerciseQTemplate(OrderingQTemplate(doubleProp, GtOp))
  //   exerciseQTemplate(OrderingQTemplate(floatProp, GteOp))
  //   exerciseQTemplate(OrderingQTemplate(intProp, LtOp))
  //   exerciseQTemplate(OrderingQTemplate(longProp, LteOp))
  //   exerciseQTemplate(OrderingQTemplate(stringProp, GtOp))
  //   exerciseQTemplate(OrderingQTemplate(dateTimeProp, GteOp))
  // }

  // behavior of "CassandraRepo.retrieveByQuery"
  // it should "produce expected results for simple conditional queries" in {
  //   exerciseQTemplate(ConditionalQTemplate(
  //     EqualityQTemplate(floatProp, EqOp),
  //     AndOp,
  //     EqualityQTemplate(longProp, EqOp)))
  //   exerciseQTemplate(ConditionalQTemplate(
  //     OrderingQTemplate(floatProp, LteOp),
  //     AndOp,
  //     OrderingQTemplate(longProp, GtOp)))
  // }

  // behavior of "CassandraRepo.retrieveByQuery"
  // it should "produce expected results for nested conditional queries" in {
  //   exerciseQTemplate(
  //     ConditionalQTemplate(
  //       ConditionalQTemplate(
  //         EqualityQTemplate(floatProp, EqOp),
  //         AndOp,
  //         EqualityQTemplate(longProp, EqOp)),
  //       AndOp,
  //       EqualityQTemplate(dateTimeProp, EqOp)))
  //   exerciseQTemplate(
  //     ConditionalQTemplate(
  //       ConditionalQTemplate(
  //         OrderingQTemplate(floatProp, GtOp),
  //         AndOp,
  //         OrderingQTemplate(longProp, LtOp)),
  //       AndOp,
  //       OrderingQTemplate(dateTimeProp, LtOp)))
  // }

}
