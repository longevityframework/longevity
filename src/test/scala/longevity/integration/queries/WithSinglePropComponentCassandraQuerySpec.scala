package longevity.integration.queries

import longevity.test.QuerySpec
import longevity.integration.subdomain.withSinglePropComponent._
import scala.concurrent.ExecutionContext.Implicits.global

class WithSinglePropComponentCassandraQuerySpec
extends QuerySpec[WithSinglePropComponent](cassandraContext, cassandraContext.testRepoPool) {

  lazy val sample = randomP

  val uriProp = WithSinglePropComponent.prop[Uri]("uri")

  import WithSinglePropComponent.queryDsl._

  behavior of "CassandraRepo.retrieveByQuery"

  it should "produce expected results for simple equality queries" in {
    exerciseQuery(uriProp eqs sample.uri)
  }

}
