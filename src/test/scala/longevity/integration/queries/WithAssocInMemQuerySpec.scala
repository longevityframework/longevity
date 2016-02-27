package longevity.integration.queries

import longevity.subdomain.Assoc
import longevity.subdomain.root.Query._
import longevity.test.QuerySpec
import longevity.integration.subdomain.withAssoc._

class WithAssocInMemQuerySpec extends QuerySpec[WithAssoc](
  context.mongoContext,
  context.mongoContext.inMemTestRepoPool) {

  val repo = repoPool[WithAssoc]

  val uriProp = WithAssoc.prop[String]("uri")
  val associatedProp = WithAssoc.prop[Assoc[Associated]]("associated")

  behavior of "InMemRepo.retrieveByQuery"
  it should "produce expected results for simple equality queries with associations" in {
    exerciseQTemplate(EqualityQTemplate(associatedProp, EqOp))
    exerciseQTemplate(EqualityQTemplate(associatedProp, NeqOp))
  }

  behavior of "InMemRepo.retrieveByQuery"
  it should "produce expected results for simple conditional queries" in {
    exerciseQTemplate(ConditionalQTemplate(
      EqualityQTemplate(uriProp, EqOp),
      OrOp,
      EqualityQTemplate(associatedProp, EqOp)))
    exerciseQTemplate(ConditionalQTemplate(
      OrderingQTemplate(uriProp, LtOp),
      AndOp,
      EqualityQTemplate(associatedProp, EqOp)))
  }

}
