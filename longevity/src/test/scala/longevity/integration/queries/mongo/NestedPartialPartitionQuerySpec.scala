package longevity.integration.queries.mongo

import longevity.effect.Blocking
import longevity.TestLongevityConfigs
import longevity.context.LongevityContext
import longevity.test.QuerySpec
import longevity.integration.model.primaryKeyInComponentWithPartialPartition._

class NestedPartialPartitionQuerySpec
    extends QuerySpec[Blocking, DomainModel, PKInComponentWithPartialPartition](
  new LongevityContext(TestLongevityConfigs.mongoConfig)) {

  lazy val sample = randomP
  val keyProp = PKInComponentWithPartialPartition.props.component.key
  import PKInComponentWithPartialPartition.queryDsl._

  behavior of "MongoPRepo.retrieveByQuery"

  it should "produce expected results for simple equality queries" in {
    exerciseQuery(keyProp eqs sample.component.key)
    exerciseQuery(keyProp neq sample.component.key)
    exerciseQuery(keyProp lt  sample.component.key)
    exerciseQuery(keyProp lte sample.component.key)
    exerciseQuery(keyProp gt  sample.component.key)
    exerciseQuery(keyProp gte sample.component.key)
  }

}
