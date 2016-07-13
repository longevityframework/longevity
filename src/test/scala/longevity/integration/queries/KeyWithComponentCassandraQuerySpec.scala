package longevity.integration.queries

import longevity.test.QuerySpec
import longevity.integration.subdomain.keyWithComponent._
import scala.concurrent.ExecutionContext.Implicits.global

class KeyWithComponentCassandraQuerySpec
extends QuerySpec[KeyWithComponent](cassandraContext, cassandraContext.testRepoPool) {

  lazy val sample = randomP

  val secondaryKeyProp = KeyWithComponent.props.secondaryKey

  import KeyWithComponent.queryDsl._

  behavior of "CassandraRepo.retrieveByQuery"

  it should "produce expected results for simple equality queries" in {
    exerciseQuery(secondaryKeyProp eqs sample.secondaryKey)
  }

}
