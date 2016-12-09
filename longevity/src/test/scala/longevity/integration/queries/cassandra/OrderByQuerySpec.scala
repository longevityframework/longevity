package longevity.integration.queries.cassandra

import com.datastax.driver.core.exceptions.InvalidQueryException
import longevity.TestLongevityConfigs
import longevity.context.LongevityContext
import longevity.integration.model.partitionKeyWithComplexPartialPartition
import longevity.model.query.Query
import longevity.test.QuerySpec
import partitionKeyWithComplexPartialPartition.PartitionKeyWithComplexPartialPartition
import partitionKeyWithComplexPartialPartition.subdomain
import scala.concurrent.ExecutionContext.{ global => globalExecutionContext }

class OrderByQuerySpec extends QuerySpec[PartitionKeyWithComplexPartialPartition](
  new LongevityContext(subdomain, TestLongevityConfigs.cassandraConfig))(
  PartitionKeyWithComplexPartialPartition.pTypeKey,
  globalExecutionContext) {

  lazy val keyProp1 = longevityContext.testDataGenerator.generate[String]
  lazy val subKeyProp1 = longevityContext.testDataGenerator.generate[String]

  override protected def generateP(): PartitionKeyWithComplexPartialPartition = {
    val raw = super.generateP()
    val subKey = raw.key.subKey.copy(prop1 = subKeyProp1)
    val key = raw.key.copy(prop1 = keyProp1, subKey = subKey)
    raw.copy(key = key)
  }

  lazy val sample = randomP

  import PartitionKeyWithComplexPartialPartition.queryDsl._
  import PartitionKeyWithComplexPartialPartition.props

  behavior of "CassandraRepo.retrieveByQuery"

  it should "handle order by clauses in very limited circumstances" in {
    // those circumstances are:
    //   - the entire partition of the partition key has to be in the filter with eqs
    //   - only post partition props can be mentioned in the order by clause
    //   - they must be included in post partition prop order
    //   - either everything is ascending or everything is descending

    var query: Query[PartitionKeyWithComplexPartialPartition] = null

    query = props.key.prop1 eqs keyProp1 and props.key.subKey.prop1 eqs subKeyProp1 orderBy (props.key.subKey.prop2.asc)
    exerciseQuery(query)

    query =
      props.key.prop1        eqs keyProp1    and
      props.key.subKey.prop1 eqs subKeyProp1 and
      props.key.subKey.prop2 lt  sample.key.subKey.prop2 orderBy (props.key.subKey.prop2.asc)
    exerciseQuery(query)

    // this one works on Cassandra 3.7 but not on the Cassandra 2 version found on Travis-ci.org:
    // query =
    //   props.key.prop1 eqs keyProp1 and
    //   props.key.subKey.prop1 eqs subKeyProp1 and
    //   props.key.prop2 lt sample.key.prop2 orderBy (props.key.subKey.prop2.asc)
    // exerciseQuery(query)

    query = props.key.prop1 eqs keyProp1 and props.key.subKey.prop1 eqs subKeyProp1 orderBy (props.key.subKey.prop2.desc)
    exerciseQuery(query)

    query = props.key.prop1 eqs keyProp1 and props.key.subKey.prop1 eqs subKeyProp1 orderBy (
      props.key.subKey.prop2.desc,
      props.key.prop2.desc)
    exerciseQuery(query)

  }

  it should "throw cassandra exceptions on any number of invalid order by queries" in {
    var query: Query[PartitionKeyWithComplexPartialPartition] = null

    query = props.key.prop1 eqs keyProp1 orderBy (props.key.subKey.prop2.asc)
    repo.retrieveByQuery(query).failed.futureValue shouldBe an [InvalidQueryException]

    query = props.key.prop1 eqs keyProp1 and props.key.subKey.prop1 lt subKeyProp1 orderBy (props.key.subKey.prop2)
    repo.retrieveByQuery(query).failed.futureValue shouldBe an [InvalidQueryException]

    query = props.key.prop1 eqs keyProp1 and props.key.subKey.prop1 eqs subKeyProp1 orderBy (props.key.subKey.prop1)
    repo.retrieveByQuery(query).failed.futureValue shouldBe an [InvalidQueryException]

    query = props.key.prop1 eqs keyProp1 and props.key.subKey.prop1 eqs subKeyProp1 orderBy (props.key.prop2)
    repo.retrieveByQuery(query).failed.futureValue shouldBe an [InvalidQueryException]

    query = props.key.prop1 eqs keyProp1 and props.key.subKey.prop1 eqs subKeyProp1 orderBy (
      props.key.subKey.prop2.desc,
      props.key.prop2.asc)
    repo.retrieveByQuery(query).failed.futureValue shouldBe an [InvalidQueryException]

  }

}
