package longevity.integration.queries

import longevity.ConfigMatrixKey
import longevity.TestLongevityConfigs
import longevity.context.Cassandra
import longevity.context.LongevityContext
import longevity.exceptions.persistence.cassandra.CompoundPropInOrderingQuery
import longevity.test.QuerySpec
import longevity.integration.subdomain.component._
import scala.concurrent.ExecutionContext.Implicits.global

class ComponentCassandraQuerySpec extends QuerySpec[WithComponent](
  new LongevityContext(
    subdomain,
    TestLongevityConfigs.configMatrix(ConfigMatrixKey(Cassandra, false, false)))) {

  lazy val sample = randomP

  val componentProp = WithComponent.props.component

  import WithComponent.queryDsl._

  behavior of "CassandraRepo.retrieveByQuery"

  it should "produce expected results for simple equality queries" in {
    exerciseQuery(componentProp eqs sample.component)
  }

  it should "throw exception for an ordering query on a component" in {
    repo.retrieveByQuery(
      componentProp lt sample.component
    ).failed.futureValue shouldBe a [CompoundPropInOrderingQuery]
  }

}
