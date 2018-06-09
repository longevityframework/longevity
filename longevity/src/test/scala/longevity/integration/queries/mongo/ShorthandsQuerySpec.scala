package longevity.integration.queries.mongo

import longevity.effect.Blocking
import longevity.TestLongevityConfigs
import longevity.context.LongevityContext
import longevity.test.QuerySpec
import longevity.integration.model.shorthands._

class ShorthandsQuerySpec extends QuerySpec[Blocking, DomainModel, Shorthands](
  new LongevityContext(TestLongevityConfigs.mongoConfig)) {

  lazy val sample = randomP

  val idProp = Shorthands.props.id

  import Shorthands.queryDsl._

  behavior of "MongoPRepo.retrieveByQuery"

  it should "produce expected results for simple equality queries" in {
    exerciseQuery(idProp eqs sample.id)
    exerciseQuery(idProp neq sample.id)
  }

  it should "produce expected results for simple ordering queries" in {
    exerciseQuery(idProp gt sample.id)
    exerciseQuery(idProp gte sample.id)
    exerciseQuery(idProp lt sample.id)
    exerciseQuery(idProp lte sample.id)
  }

}
