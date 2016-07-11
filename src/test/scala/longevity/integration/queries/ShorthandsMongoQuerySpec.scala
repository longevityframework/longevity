package longevity.integration.queries

import longevity.test.QuerySpec
import longevity.integration.subdomain.shorthands._
import scala.concurrent.ExecutionContext.Implicits.global

class ShorthandsMongoQuerySpec
extends QuerySpec[Shorthands](mongoContext, mongoContext.testRepoPool) {

  lazy val sample = randomP

  val idProp = Shorthands.prop[ShorthandsId]("id")

  import Shorthands.queryDsl._

  behavior of "MongoRepo.retrieveByQuery"

  it should "produce expected results for simple equality queries" in {
    exerciseQuery(idProp eqs sample.id)
    exerciseQuery(idProp neq sample.id)
  }

  it should "produce expected results for simple ordering queries" in {
    exerciseQuery(idProp gt sample.id)
    exerciseQuery(idProp gte sample.id)
    exerciseQuery(idProp lt sample.id)
    exerciseQuery(idProp lte sample.id)
  }

}
