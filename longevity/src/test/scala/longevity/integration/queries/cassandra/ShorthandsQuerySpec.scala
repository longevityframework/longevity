package longevity.integration.queries.cassandra

import longevity.TestLongevityConfigs
import longevity.context.LongevityContext
import longevity.test.QuerySpec
import longevity.integration.model.shorthands._
import longevity.integration.queries.queryTestsExecutionContext

class ShorthandsQuerySpec extends QuerySpec[DomainModel, Shorthands](
  new LongevityContext[DomainModel](TestLongevityConfigs.cassandraConfig)) {

  lazy val sample = randomP

  val idProp = Shorthands.props.id

  import Shorthands.queryDsl._

  behavior of "CassandraRepo.retrieveByQuery"

  it should "produce expected results for simple equality queries" in {
    exerciseQuery(idProp eqs sample.id)
  }

}
