package longevity.integration.queries.cassandra

import longevity.TestLongevityConfigs
import longevity.context.LongevityContext
import longevity.test.QuerySpec
import longevity.integration.model.shorthands._
import scala.concurrent.ExecutionContext.Implicits.global

class ShorthandsQuerySpec extends QuerySpec[Shorthands](
  new LongevityContext(subdomain, TestLongevityConfigs.cassandraConfig)) {

  lazy val sample = randomP

  val idProp = Shorthands.props.id

  import Shorthands.queryDsl._

  behavior of "CassandraRepo.retrieveByQuery"

  it should "produce expected results for simple equality queries" in {
    exerciseQuery(idProp eqs sample.id)
  }

}
