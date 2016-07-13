package longevity.integration.queries

import longevity.test.QuerySpec
import longevity.integration.subdomain.shorthandWithComponent._
import scala.concurrent.ExecutionContext.Implicits.global

class ShorthandWithComponentMongoQuerySpec
extends QuerySpec[WithShorthandWithComponent](mongoContext, mongoContext.testRepoPool) {

  lazy val sample = randomP

  val shorthandWithComponentProp =
    WithShorthandWithComponent.props.shorthandWithComponent

  import WithShorthandWithComponent.queryDsl._

  behavior of "MongoRepo.retrieveByQuery"

  it should "produce expected results for simple equality queries" in {
    exerciseQuery(shorthandWithComponentProp eqs sample.shorthandWithComponent)
    exerciseQuery(shorthandWithComponentProp neq sample.shorthandWithComponent)
  }

  it should "produce expected results for simple ordering queries" in {
    exerciseQuery(shorthandWithComponentProp gt sample.shorthandWithComponent)
    exerciseQuery(shorthandWithComponentProp gte sample.shorthandWithComponent)
    exerciseQuery(shorthandWithComponentProp lt sample.shorthandWithComponent)
    exerciseQuery(shorthandWithComponentProp lte sample.shorthandWithComponent)
  }

}
