package longevity.integration.queries

import longevity.subdomain.Assoc
import longevity.test.QuerySpec
import longevity.integration.subdomain.withAssoc._

class WithAssocInMemQuerySpec extends QuerySpec[WithAssoc](
  context.mongoContext,
  context.mongoContext.inMemTestRepoPool) {

  val repo = repoPool[WithAssoc]
  lazy val sample = randomRoot

  val uriProp = WithAssoc.prop[String]("uri")
  val associatedProp = WithAssoc.prop[Assoc[Associated]]("associated")

  import WithAssoc.queryDsl._

  behavior of "InMemRepo.retrieveByQuery"
  it should "produce expected results for simple equality queries with associations" in {
    exerciseQuery(associatedProp eqs sample.associated)
    exerciseQuery(associatedProp neq sample.associated)
  }

  behavior of "InMemRepo.retrieveByQuery"
  it should "produce expected results for simple conditional queries" in {
    exerciseQuery(uriProp eqs sample.uri or associatedProp eqs sample.associated)
    exerciseQuery(uriProp lt sample.uri and associatedProp neq sample.associated)
  }

}
