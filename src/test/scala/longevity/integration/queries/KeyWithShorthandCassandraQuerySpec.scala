package longevity.integration.queries

import longevity.test.QuerySpec
import longevity.integration.subdomain.keyWithShorthand._
import scala.concurrent.ExecutionContext.Implicits.global

class KeyWithShorthandCassandraQuerySpec
extends QuerySpec[KeyWithShorthand](cassandraContext, cassandraContext.testRepoPool) {

  lazy val sample = randomP

  val secondaryKeyProp = KeyWithShorthand.prop[SecondaryKey]("secondaryKey")

  import KeyWithShorthand.queryDsl._

  behavior of "CassandraRepo.retrieveByQuery"

  it should "produce expected results for simple equality queries" in {
    exerciseQuery(secondaryKeyProp eqs sample.secondaryKey)
  }

}
