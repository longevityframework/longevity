package longevity.integration.queries

import longevity.subdomain.Assoc
import longevity.test.QuerySpec
import longevity.integration.subdomain.withAssoc._
import scala.concurrent.ExecutionContext.Implicits.global

class WithAssocCassandraQuerySpec extends QuerySpec[WithAssoc](
  cassandraContext,
  cassandraContext.testRepoPool) {

  lazy val sample = randomP

  val uriProp = WithAssoc.prop[String]("uri")
  val associatedProp = WithAssoc.prop[Assoc[Associated]]("associated")

  import WithAssoc.queryDsl._

  behavior of "InMemRepo.retrieveByQuery"
  it should "produce expected results for simple equality queries with associations" in {
    exerciseQuery(associatedProp eqs sample.associated)
  }

  behavior of "InMemRepo.retrieveByQuery"
  it should "produce expected results for simple conditional queries" in {
    exerciseQuery(uriProp eqs sample.uri and associatedProp eqs sample.associated)
  }

}
