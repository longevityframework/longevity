package longevity.integration.queries.mongo

import longevity.effect.Blocking
import longevity.TestLongevityConfigs
import longevity.context.LongevityContext
import longevity.integration.model.foreignKey._
import longevity.test.QuerySpec

class WithForeignKeyQuerySpec extends QuerySpec[Blocking, DomainModel, WithForeignKey](
  new LongevityContext(TestLongevityConfigs.mongoConfig)) {

  lazy val sample = randomP

  val idProp = WithForeignKey.props.id
  val associatedProp = WithForeignKey.props.associated

  import WithForeignKey.queryDsl._

  behavior of "MongoPRepo.retrieveByQuery"
  it should "produce expected results for simple equality queries with foreign keys" in {
    exerciseQuery(associatedProp eqs sample.associated)
    exerciseQuery(associatedProp neq sample.associated)
  }

  behavior of "MongoPRepo.retrieveByQuery"
  it should "produce expected results for simple conditional queries" in {
    exerciseQuery(idProp eqs sample.id or associatedProp eqs sample.associated)
    exerciseQuery(idProp lt sample.id and associatedProp neq sample.associated)
  }

}
