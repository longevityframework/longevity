package longevity.integration.queries

import longevity.test.QuerySpec
import longevity.integration.subdomain.shorthandWithComponent._
import scala.concurrent.ExecutionContext.Implicits.global

class ShorthandWithComponentCassandraQuerySpec
extends QuerySpec[WithShorthandWithComponent](cassandraContext, cassandraContext.testRepoPool) {

  lazy val sample = randomP

  val shorthandWithComponentProp =
    WithShorthandWithComponent.prop[ShorthandWithComponent]("shorthandWithComponent")

  import WithShorthandWithComponent.queryDsl._

  behavior of "CassandraRepo.retrieveByQuery"

  it should "produce expected results for simple equality queries" in {
    exerciseQuery(shorthandWithComponentProp eqs sample.shorthandWithComponent)
  }

}
