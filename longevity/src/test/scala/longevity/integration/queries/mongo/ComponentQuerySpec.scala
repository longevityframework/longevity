package longevity.integration.queries.mongo

import longevity.effect.Blocking
import longevity.TestLongevityConfigs
import longevity.context.LongevityContext
import longevity.test.QuerySpec
import longevity.integration.model.component._

class ComponentQuerySpec extends QuerySpec[Blocking, DomainModel, WithComponent](
  new LongevityContext(TestLongevityConfigs.mongoConfig)) {

  lazy val sample = randomP

  val componentProp = WithComponent.props.component

  import WithComponent.queryDsl._

  behavior of "MongoPRepo.retrieveByQuery"

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
