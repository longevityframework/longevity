package longevity.integration.queries.cassandra

import longevity.TestLongevityConfigs
import longevity.context.LongevityContext
import longevity.test.QuerySpec
import longevity.integration.model.shorthandWithComponent._
import longevity.integration.queries.queryTestsExecutionContext
import longevity.integration.queries.queryTestsExecutionContext
import scala.concurrent.Future

class ShorthandWithComponentQuerySpec extends QuerySpec[Future, DomainModel, WithShorthandWithComponent](
  new LongevityContext(TestLongevityConfigs.cassandraConfig)) {

  lazy val sample = randomP

  val shorthandWithComponentProp =
    WithShorthandWithComponent.props.shorthandWithComponent

  import WithShorthandWithComponent.queryDsl._

  behavior of "CassandraPRepo.retrieveByQuery"

  it should "produce expected results for simple equality queries" in {
    exerciseQuery(shorthandWithComponentProp eqs sample.shorthandWithComponent)
  }

}
