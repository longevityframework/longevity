package longevity.integration.queries

import longevity.test.QuerySpec
import longevity.integration.subdomain.shorthands._
import scala.concurrent.ExecutionContext.Implicits.global

class ShorthandsCassandraQuerySpec
extends QuerySpec[Shorthands](cassandraContext, cassandraContext.testRepoPool) {

  lazy val sample = randomP

  val idProp = Shorthands.prop[ShorthandsId]("id")

  import Shorthands.queryDsl._

  behavior of "CassandraRepo.retrieveByQuery"

  it should "produce expected results for simple equality queries" in {
    exerciseQuery(idProp eqs sample.id)
  }

}
