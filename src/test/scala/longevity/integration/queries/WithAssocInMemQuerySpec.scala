package longevity.integration.queries

import longevity.subdomain.Assoc
import longevity.test.QuerySpec
import longevity.integration.subdomain.withAssoc._

class WithAssocInMemQuerySpec extends QuerySpec[WithAssoc](mongoContext, mongoContext.inMemTestRepoPool) {

  val repo = repoPool[WithAssoc]

  val uriProp = WithAssoc.prop[String]("uri")
  val associatedProp = WithAssoc.prop[Assoc[Associated]]("associated")

  behavior of "InMemRepo.retrieveByQuery"
  it should "produce expected results for simple equality queries with associations" in {
    exerciseQTemplate(EqualityQTemplate(associatedProp))
  }

  behavior of "InMemRepo.retrieveByQuery"
  it should "produce expected results for simple conditional queries" in {
    exerciseQTemplate(ConditionalQTemplate(EqualityQTemplate(uriProp), EqualityQTemplate(associatedProp)))
    exerciseQTemplate(ConditionalQTemplate(OrderingQTemplate(uriProp), EqualityQTemplate(associatedProp)))
  }

}
