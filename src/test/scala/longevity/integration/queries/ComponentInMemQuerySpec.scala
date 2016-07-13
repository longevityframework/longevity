package longevity.integration.queries

import longevity.test.QuerySpec
import longevity.integration.subdomain.component._
import scala.concurrent.ExecutionContext.Implicits.global

class ComponentInMemQuerySpec
extends QuerySpec[WithComponent](mongoContext, mongoContext.inMemTestRepoPool) {

  lazy val sample = randomP

  val componentProp = WithComponent.props.component

  import WithComponent.queryDsl._

  behavior of "InMemRepo.retrieveByQuery"

  it should "produce expected results for simple equality queries" in {
    exerciseQuery(componentProp eqs sample.component)
    exerciseQuery(componentProp neq sample.component)
  }

  it should "produce expected results for simple ordering queries" in {
    exerciseQuery(componentProp gt sample.component)
    exerciseQuery(componentProp gte sample.component)
    exerciseQuery(componentProp lt sample.component)
    exerciseQuery(componentProp lte sample.component)
  }

}
