package longevity.integration.queries

import longevity.test.QuerySpec
import longevity.integration.subdomain.withSinglePropComponent._
import scala.concurrent.ExecutionContext.Implicits.global

class WithSinglePropComponentInMemQuerySpec
extends QuerySpec[WithSinglePropComponent](mongoContext, mongoContext.inMemTestRepoPool) {

  lazy val sample = randomP

  val uriProp = WithSinglePropComponent.prop[Uri]("uri")

  import WithSinglePropComponent.queryDsl._

  behavior of "InMemRepo.retrieveByQuery"

  it should "produce expected results for simple equality queries" in {
    exerciseQuery(uriProp eqs sample.uri)
    exerciseQuery(uriProp neq sample.uri)
  }

  it should "produce expected results for simple ordering queries" in {
    exerciseQuery(uriProp gt sample.uri)
    exerciseQuery(uriProp gte sample.uri)
    exerciseQuery(uriProp lt sample.uri)
    exerciseQuery(uriProp lte sample.uri)
  }

}
