package longevity.integration.queries

import longevity.test.QuerySpec
import longevity.integration.subdomain.keyWithComponent._
import scala.concurrent.ExecutionContext.Implicits.global

class KeyWithComponentInMemQuerySpec
extends QuerySpec[KeyWithComponent](mongoContext, mongoContext.inMemTestRepoPool) {

  lazy val sample = randomP

  val secondaryKeyProp = KeyWithComponent.props.secondaryKey

  import KeyWithComponent.queryDsl._

  behavior of "InMemRepo.retrieveByQuery"

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
