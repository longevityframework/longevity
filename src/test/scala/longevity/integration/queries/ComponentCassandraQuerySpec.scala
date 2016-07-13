package longevity.integration.queries

import longevity.test.QuerySpec
import longevity.integration.subdomain.component._
import scala.concurrent.ExecutionContext.Implicits.global

class ComponentCassandraQuerySpec
extends QuerySpec[WithComponent](cassandraContext, cassandraContext.testRepoPool) {

  lazy val sample = randomP

  val componentProp = WithComponent.props.component

  import WithComponent.queryDsl._

  behavior of "CassandraRepo.retrieveByQuery"

  it should "produce expected results for simple equality queries" in {
    exerciseQuery(componentProp eqs sample.component)
  }

}
