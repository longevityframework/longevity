package longevity.integration.queries.cassandra

import longevity.effect.Blocking
import longevity.TestLongevityConfigs
import longevity.context.LongevityContext
import longevity.test.QuerySpec
import longevity.integration.model.shorthandWithComponent._

class ShorthandWithComponentQuerySpec extends QuerySpec[Blocking, DomainModel, WithShorthandWithComponent](
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
