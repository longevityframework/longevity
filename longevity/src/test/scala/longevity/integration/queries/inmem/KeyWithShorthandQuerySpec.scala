package longevity.integration.queries.inmem

import longevity.effect.Blocking
import longevity.TestLongevityConfigs
import longevity.context.LongevityContext
import longevity.test.QuerySpec
import longevity.integration.model.keyWithShorthand._

class KeyWithShorthandQuerySpec extends QuerySpec[Blocking, DomainModel, KeyWithShorthand](
  new LongevityContext(TestLongevityConfigs.inMemConfig)) {

  lazy val sample = randomP

  val secondaryKeyProp = KeyWithShorthand.props.secondaryKey

  import KeyWithShorthand.queryDsl._

  behavior of "InMemPRepo.retrieveByQuery"

  it should "produce expected results for simple equality queries" in {
    exerciseQuery(secondaryKeyProp eqs sample.secondaryKey)
    exerciseQuery(secondaryKeyProp neq sample.secondaryKey)
  }

  it should "produce expected results for simple ordering queries" in {
    exerciseQuery(secondaryKeyProp gt sample.secondaryKey)
    exerciseQuery(secondaryKeyProp gte sample.secondaryKey)
    exerciseQuery(secondaryKeyProp lt sample.secondaryKey)
    exerciseQuery(secondaryKeyProp lte sample.secondaryKey)
  }

}
