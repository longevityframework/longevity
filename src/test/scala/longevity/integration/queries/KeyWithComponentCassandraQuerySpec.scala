package longevity.integration.queries

import longevity.ConfigMatrixKey
import longevity.TestLongevityConfigs
import longevity.context.Cassandra
import longevity.context.LongevityContext
import longevity.test.QuerySpec
import longevity.integration.subdomain.keyWithComponent._
import scala.concurrent.ExecutionContext.Implicits.global

class KeyWithComponentCassandraQuerySpec extends QuerySpec[KeyWithComponent](
  new LongevityContext(
    subdomain,
    TestLongevityConfigs.configMatrix(ConfigMatrixKey(Cassandra, false, false)))) {

  lazy val sample = randomP

  val secondaryKeyProp = KeyWithComponent.props.secondaryKey

  import KeyWithComponent.queryDsl._

  behavior of "CassandraRepo.retrieveByQuery"

  it should "produce expected results for simple equality queries" in {
    exerciseQuery(secondaryKeyProp eqs sample.secondaryKey)
  }

}
