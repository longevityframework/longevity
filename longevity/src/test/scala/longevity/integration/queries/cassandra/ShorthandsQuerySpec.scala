package longevity.integration.queries.cassandra

import longevity.effect.Blocking
import longevity.TestLongevityConfigs
import longevity.context.LongevityContext
import longevity.test.QuerySpec
import longevity.integration.model.shorthands._

class ShorthandsQuerySpec extends QuerySpec[Blocking, DomainModel, Shorthands](
  new LongevityContext(TestLongevityConfigs.cassandraConfig)) {

  lazy val sample = randomP

  val idProp = Shorthands.props.id

  import Shorthands.queryDsl._

  behavior of "CassandraPRepo.retrieveByQuery"

  it should "produce expected results for simple equality queries" in {
    exerciseQuery(idProp eqs sample.id)
  }

}
