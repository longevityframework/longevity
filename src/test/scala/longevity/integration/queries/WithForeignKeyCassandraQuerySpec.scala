package longevity.integration.queries

import longevity.test.QuerySpec
import longevity.integration.subdomain.withForeignKey._
import scala.concurrent.ExecutionContext.Implicits.global

class WithForeignKeyCassandraQuerySpec extends QuerySpec[WithForeignKey](
  cassandraContext,
  cassandraContext.testRepoPool) {

  lazy val sample = randomP

  val idProp = WithForeignKey.props.id
  val associatedProp = WithForeignKey.props.associated

  import WithForeignKey.queryDsl._

  behavior of "InMemRepo.retrieveByQuery"
  it should "produce expected results for simple equality queries with associations" in {
    exerciseQuery(associatedProp eqs sample.associated)
  }

  behavior of "InMemRepo.retrieveByQuery"
  it should "produce expected results for simple conditional queries" in {
    exerciseQuery(idProp eqs sample.id and associatedProp eqs sample.associated)
  }

}
