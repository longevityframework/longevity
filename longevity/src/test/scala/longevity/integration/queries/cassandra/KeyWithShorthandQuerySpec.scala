package longevity.integration.queries.cassandra

import longevity.TestLongevityConfigs
import longevity.context.LongevityContext
import longevity.test.QuerySpec
import longevity.integration.model.keyWithShorthand._
import scala.concurrent.ExecutionContext.Implicits.global

class KeyWithShorthandQuerySpec extends QuerySpec[KeyWithShorthand](
  new LongevityContext(domainModel, TestLongevityConfigs.cassandraConfig)) {

  lazy val sample = randomP

  val secondaryKeyProp = KeyWithShorthand.props.secondaryKey

  import KeyWithShorthand.queryDsl._

  behavior of "CassandraRepo.retrieveByQuery"

  it should "produce expected results for simple equality queries" in {
    exerciseQuery(secondaryKeyProp eqs sample.secondaryKey)
  }

}
