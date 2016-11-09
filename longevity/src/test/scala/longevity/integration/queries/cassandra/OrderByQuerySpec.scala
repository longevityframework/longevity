package longevity.integration.queries.cassandra

import com.datastax.driver.core.exceptions.InvalidQueryException
import longevity.TestLongevityConfigs
import longevity.context.LongevityContext
import longevity.integration.subdomain.partitionKeyWithComplexPartialPartition
import longevity.subdomain.query.Query
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

    query = props.keyProp1 eqs keyProp1 and props.subKeyProp1 eqs subKeyProp1 orderBy (props.subKeyProp2.asc)
    exerciseQuery(query)

    query =
      props.keyProp1 eqs keyProp1 and
      props.subKeyProp1 eqs subKeyProp1 and
      props.subKeyProp2 lt sample.key.subKey.prop2 orderBy (props.subKeyProp2.asc)
    exerciseQuery(query)

    // this one works on Cassandra 3.7 but not on the Cassandra 2 version found on Travis-ci.org:
    // query =
    //   props.keyProp1 eqs keyProp1 and
    //   props.subKeyProp1 eqs subKeyProp1 and
    //   props.keyProp2 lt sample.key.prop2 orderBy (props.subKeyProp2.asc)
    // exerciseQuery(query)

    query = props.keyProp1 eqs keyProp1 and props.subKeyProp1 eqs subKeyProp1 orderBy (props.subKeyProp2.desc)
    exerciseQuery(query)

    query = props.keyProp1 eqs keyProp1 and props.subKeyProp1 eqs subKeyProp1 orderBy (
      props.subKeyProp2.desc,
      props.keyProp2.desc)
    exerciseQuery(query)

  }

  it should "throw cassandra exceptions on any number of invalid order by queries" in {
    var query: Query[PartitionKeyWithComplexPartialPartition] = null

    query = props.keyProp1 eqs keyProp1 orderBy (props.subKeyProp2.asc)
    repo.retrieveByQuery(query).failed.futureValue shouldBe an [InvalidQueryException]

    query = props.keyProp1 eqs keyProp1 and props.subKeyProp1 lt subKeyProp1 orderBy (props.subKeyProp2)
    repo.retrieveByQuery(query).failed.futureValue shouldBe an [InvalidQueryException]

    query = props.keyProp1 eqs keyProp1 and props.subKeyProp1 eqs subKeyProp1 orderBy (props.subKeyProp1)
    repo.retrieveByQuery(query).failed.futureValue shouldBe an [InvalidQueryException]

    query = props.keyProp1 eqs keyProp1 and props.subKeyProp1 eqs subKeyProp1 orderBy (props.keyProp2)
    repo.retrieveByQuery(query).failed.futureValue shouldBe an [InvalidQueryException]

    query = props.keyProp1 eqs keyProp1 and props.subKeyProp1 eqs subKeyProp1 orderBy (
      props.subKeyProp2.desc,
      props.keyProp2.asc)
    repo.retrieveByQuery(query).failed.futureValue shouldBe an [InvalidQueryException]

  }

}
