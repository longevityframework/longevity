package longevity.integration.queries.cassandra

import longevity.TestLongevityConfigs
import longevity.context.LongevityContext
import longevity.integration.model.foreignKey._
import longevity.test.QuerySpec
import longevity.integration.queries.queryTestsExecutionContext

class WithForeignKeyQuerySpec extends QuerySpec[WithForeignKey](
  new LongevityContext(domainModel, TestLongevityConfigs.cassandraConfig)) {

  lazy val sample = randomP

  val idProp = WithForeignKey.props.id
  val associatedProp = WithForeignKey.props.associated

  import WithForeignKey.queryDsl._

  behavior of "CassandraRepo.retrieveByQuery"
  it should "produce expected results for simple equality queries with foreign keys" in {
    exerciseQuery(associatedProp eqs sample.associated)
  }

  behavior of "CassandraRepo.retrieveByQuery"
  it should "produce expected results for simple conditional queries" in {
    exerciseQuery(idProp eqs sample.id and associatedProp eqs sample.associated)
  }

}
