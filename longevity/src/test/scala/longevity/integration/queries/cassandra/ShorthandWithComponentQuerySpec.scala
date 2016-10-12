package longevity.integration.queries.cassandra

import longevity.TestLongevityConfigs
import longevity.context.LongevityContext
import longevity.test.QuerySpec
import longevity.integration.subdomain.shorthandWithComponent._
import scala.concurrent.ExecutionContext.Implicits.global

class ShorthandWithComponentQuerySpec
extends QuerySpec[WithShorthandWithComponent](
  new LongevityContext(subdomain, TestLongevityConfigs.cassandraConfig)) {

  lazy val sample = randomP

  val shorthandWithComponentProp =
    WithShorthandWithComponent.props.shorthandWithComponent

  import WithShorthandWithComponent.queryDsl._

  behavior of "CassandraRepo.retrieveByQuery"

  it should "produce expected results for simple equality queries" in {
    exerciseQuery(shorthandWithComponentProp eqs sample.shorthandWithComponent)
  }

}
