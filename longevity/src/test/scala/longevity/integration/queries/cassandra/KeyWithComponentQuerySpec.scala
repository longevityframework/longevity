package longevity.integration.queries.cassandra

import longevity.TestLongevityConfigs
import longevity.context.LongevityContext
import longevity.test.QuerySpec
import longevity.integration.model.keyWithComponent._
import scala.concurrent.ExecutionContext.Implicits.global

class KeyWithComponentQuerySpec extends QuerySpec[KeyWithComponent](
  new LongevityContext(subdomain, TestLongevityConfigs.cassandraConfig)) {

  lazy val sample = randomP

  val secondaryKeyProp = KeyWithComponent.props.secondaryKey

  import KeyWithComponent.queryDsl._

  behavior of "CassandraRepo.retrieveByQuery"

  it should "produce expected results for simple equality queries" in {
    exerciseQuery(secondaryKeyProp eqs sample.secondaryKey)
  }

}
