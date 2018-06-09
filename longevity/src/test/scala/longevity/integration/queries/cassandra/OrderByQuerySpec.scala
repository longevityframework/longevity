package longevity.integration.queries.cassandra

import longevity.effect.Blocking
import com.datastax.driver.core.exceptions.InvalidQueryException
import longevity.TestLongevityConfigs
import longevity.context.LongevityContext
import longevity.integration.model.primaryKeyWithComplexPartialPartition
import longevity.model.query.Query
import longevity.test.QuerySpec
import primaryKeyWithComplexPartialPartition.PrimaryKeyWithComplexPartialPartition
import primaryKeyWithComplexPartialPartition.DomainModel

class OrderByQuerySpec extends QuerySpec[Blocking, DomainModel, PrimaryKeyWithComplexPartialPartition](
  new LongevityContext(TestLongevityConfigs.cassandraConfig)) {

  lazy val keyProp1 = longevityContext.testDataGenerator.generateString
  lazy val subKeyProp1 = longevityContext.testDataGenerator.generateString

  override protected def generateP(): PrimaryKeyWithComplexPartialPartition = {
    val raw = super.generateP()
    val subKey = raw.key.subKey.copy(prop1 = subKeyProp1)
    val key = raw.key.copy(prop1 = keyProp1, subKey = subKey)
    raw.copy(key = key)
  }

  lazy val sample = randomP

  import PrimaryKeyWithComplexPartialPartition.queryDsl._
  import PrimaryKeyWithComplexPartialPartition.props

  behavior of "CassandraPRepo.queryToVector"

  it should "handle order by clauses in very limited circumstances" in {
    // those circumstances are:
    //   - the entire partition of the primary key has to be in the filter with eqs
    //   - only post partition props can be mentioned in the order by clause
    //   - they must be included in post partition prop order
    //   - either everything is ascending or everything is descending

    var query: Query[PrimaryKeyWithComplexPartialPartition] = null

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
    var query: Query[PrimaryKeyWithComplexPartialPartition] = null

    query = props.key.prop1 eqs keyProp1 orderBy (props.key.subKey.prop2.asc)
    intercept[InvalidQueryException](effect.run(repo.queryToVector(query)))

    query = props.key.prop1 eqs keyProp1 and props.key.subKey.prop1 lt subKeyProp1 orderBy {
      props.key.subKey.prop2
    }
    intercept[InvalidQueryException](effect.run(repo.queryToVector(query)))

    query = props.key.prop1 eqs keyProp1 and props.key.subKey.prop1 eqs subKeyProp1 orderBy {
      props.key.subKey.prop1
    }
    intercept[InvalidQueryException](effect.run(repo.queryToVector(query)))

    query = props.key.prop1 eqs keyProp1 and props.key.subKey.prop1 eqs subKeyProp1 orderBy (props.key.prop2)
    intercept[InvalidQueryException](effect.run(repo.queryToVector(query)))

    query = props.key.prop1 eqs keyProp1 and props.key.subKey.prop1 eqs subKeyProp1 orderBy (
      props.key.subKey.prop2.desc,
      props.key.prop2.asc)
    intercept[InvalidQueryException](effect.run(repo.queryToVector(query)))

  }

}
