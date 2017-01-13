package longevity.integration.queries.sqlite

import longevity.TestLongevityConfigs
import longevity.context.LongevityContext
import longevity.test.QuerySpec
import longevity.integration.model.shorthandWithComponent._
import scala.concurrent.ExecutionContext.Implicits.global

class ShorthandWithComponentQuerySpec extends QuerySpec[WithShorthandWithComponent](
  new LongevityContext(domainModel, TestLongevityConfigs.sqliteConfig)) {

  lazy val sample = randomP

  val shorthandWithComponentProp =
    WithShorthandWithComponent.props.shorthandWithComponent

  import WithShorthandWithComponent.queryDsl._

  behavior of "SQLiteRepo.retrieveByQuery"

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
