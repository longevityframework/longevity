package longevity.integration.queries

import longevity.test.QuerySpec
import longevity.integration.subdomain.component._
import scala.concurrent.ExecutionContext.Implicits.global

class ComponentMongoQuerySpec
extends QuerySpec[WithComponent](mongoContext, mongoContext.testRepoPool) {

  lazy val sample = randomP

  val componentProp = WithComponent.prop[Component]("component")

  import WithComponent.queryDsl._

  behavior of "MongoRepo.retrieveByQuery"

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
