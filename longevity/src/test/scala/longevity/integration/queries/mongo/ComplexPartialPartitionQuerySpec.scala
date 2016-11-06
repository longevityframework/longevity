package longevity.integration.queries.mongo

import longevity.TestLongevityConfigs
import longevity.context.LongevityContext
import longevity.test.QuerySpec
import longevity.integration.subdomain.partitionKeyWithComplexPartialPartition._
import scala.concurrent.ExecutionContext.Implicits.global

class ComplexPartialPartitionQuerySpec extends QuerySpec[PartitionKeyWithComplexPartialPartition](
  new LongevityContext(subdomain, TestLongevityConfigs.mongoConfig)) {

  lazy val sample = randomP
  val keyProp = PartitionKeyWithComplexPartialPartition.props.key
  import PartitionKeyWithComplexPartialPartition.queryDsl._

  behavior of "MongoRepo.retrieveByQuery"

  it should "produce expected results for simple equality queries" in {
    exerciseQuery(keyProp eqs sample.key)
    exerciseQuery(keyProp neq sample.key)
    exerciseQuery(keyProp lt  sample.key)
    exerciseQuery(keyProp lte sample.key)
    exerciseQuery(keyProp gt  sample.key)
    exerciseQuery(keyProp gte sample.key)
  }

}
