package longevity.integration.queries.cassandra

import longevity.effect.Blocking
import longevity.TestLongevityConfigs
import longevity.context.LongevityContext
import longevity.test.QuerySpec
import longevity.integration.model.keyWithComponent._

class KeyWithComponentQuerySpec extends QuerySpec[Blocking, DomainModel, KeyWithComponent](
  new LongevityContext(TestLongevityConfigs.cassandraConfig)) {

  lazy val sample = randomP

  val secondaryKeyProp = KeyWithComponent.props.secondaryKey

  import KeyWithComponent.queryDsl._

  behavior of "CassandraPRepo.retrieveByQuery"

  it should "produce expected results for simple equality queries" in {
    exerciseQuery(secondaryKeyProp eqs sample.secondaryKey)
  }

}
