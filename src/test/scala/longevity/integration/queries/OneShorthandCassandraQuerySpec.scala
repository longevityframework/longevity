package longevity.integration.queries

import longevity.test.QuerySpec
import longevity.integration.subdomain.oneShorthand._
import scala.concurrent.ExecutionContext.Implicits.global

class OneShorthandCassandraQuerySpec
extends QuerySpec[OneShorthand](cassandraContext, cassandraContext.testRepoPool) {

  lazy val sample = randomP

  val uriProp = OneShorthand.prop[Uri]("uri")

  import OneShorthand.queryDsl._

  behavior of "CassandraRepo.retrieveByQuery"

  it should "produce expected results for simple equality queries" in {
    exerciseQuery(uriProp eqs sample.uri)
  }

}
