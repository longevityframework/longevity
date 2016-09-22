package longevity.integration.queries

import longevity.ConfigMatrixKey
import longevity.TestLongevityConfigs
import longevity.context.Cassandra
import longevity.context.LongevityContext
import longevity.test.QuerySpec
import longevity.integration.subdomain.shorthands._
import scala.concurrent.ExecutionContext.Implicits.global

class ShorthandsCassandraQuerySpec extends QuerySpec[Shorthands](
  new LongevityContext(
    subdomain,
    TestLongevityConfigs.configMatrix(ConfigMatrixKey(Cassandra, false, false)))) {

  lazy val sample = randomP

  val idProp = Shorthands.props.id

  import Shorthands.queryDsl._

  behavior of "CassandraRepo.retrieveByQuery"

  it should "produce expected results for simple equality queries" in {
    exerciseQuery(idProp eqs sample.id)
  }

}
