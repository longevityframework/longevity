package longevity.integration.queries.cassandra

import longevity.effect.Blocking
import longevity.TestLongevityConfigs
import longevity.context.LongevityContext
import longevity.test.QuerySpec
import longevity.integration.model.keyWithShorthand._

class KeyWithShorthandQuerySpec extends QuerySpec[Blocking, DomainModel, KeyWithShorthand](
  new LongevityContext(TestLongevityConfigs.cassandraConfig)) {

  lazy val sample = randomP

  val secondaryKeyProp = KeyWithShorthand.props.secondaryKey

  import KeyWithShorthand.queryDsl._

  behavior of "CassandraPRepo.retrieveByQuery"

  it should "produce expected results for simple equality queries" in {
    exerciseQuery(secondaryKeyProp eqs sample.secondaryKey)
  }

}
