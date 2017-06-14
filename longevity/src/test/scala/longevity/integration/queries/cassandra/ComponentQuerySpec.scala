package longevity.integration.queries.cassandra

import longevity.TestLongevityConfigs
import longevity.context.LongevityContext
import longevity.exceptions.persistence.cassandra.CompoundPropInOrderingQuery
import longevity.test.QuerySpec
import longevity.integration.model.component._
import scala.concurrent.ExecutionContext.{ global => globalExecutionContext }

class ComponentQuerySpec extends QuerySpec[DomainModel, WithComponent](
  new LongevityContext[DomainModel](TestLongevityConfigs.cassandraConfig))(
  WithComponent.pEv,
  globalExecutionContext) {

  lazy val sample = randomP

  val componentProp = WithComponent.props.component

  import WithComponent.queryDsl._

  behavior of "CassandraPRepo.queryToFutureVec"

  it should "produce expected results for simple equality queries" in {
    exerciseQuery(componentProp eqs sample.component)
  }

  it should "throw exception for an ordering query on a component" in {
    repo.queryToFutureVec(
      componentProp lt sample.component
    ).failed.futureValue shouldBe a [CompoundPropInOrderingQuery]
  }

}
