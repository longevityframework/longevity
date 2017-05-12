package longevity.integration.queries.cassandra

import longevity.TestLongevityConfigs
import longevity.context.LongevityContext
import longevity.test.QuerySpec
import longevity.integration.model.keyWithShorthand._
import longevity.integration.queries.queryTestsExecutionContext

class KeyWithShorthandQuerySpec extends QuerySpec[DomainModel, KeyWithShorthand](
  new LongevityContext[DomainModel](TestLongevityConfigs.cassandraConfig)) {

  lazy val sample = randomP

  val secondaryKeyProp = KeyWithShorthand.props.secondaryKey

  import KeyWithShorthand.queryDsl._

  behavior of "CassandraRepo.retrieveByQuery"

  it should "produce expected results for simple equality queries" in {
    exerciseQuery(secondaryKeyProp eqs sample.secondaryKey)
  }

}
