package longevity.integration.queries.cassandra

import longevity.TestLongevityConfigs
import longevity.context.LongevityContext
import longevity.exceptions.persistence.cassandra.OrderByInQueryException
import longevity.integration.subdomain.basics.Basics
import longevity.integration.subdomain.basics.subdomain
import longevity.subdomain.query.Query
import longevity.test.QuerySpec
import scala.concurrent.ExecutionContext.Implicits.global

class OrderByQuerySpec extends QuerySpec[Basics](
  new LongevityContext(subdomain, TestLongevityConfigs.cassandraConfig)) {

  lazy val sample = randomP

  import Basics.queryDsl._
  import Basics.props

  // note we will gain behavior here with partition index story:
  // https://www.pivotaltracker.com/story/show/127406611

  behavior of "CassandraRepo.retrieveByQuery"

  it should "throw OrderByInQueryException whenever the query contains an order by clause" in {
    val query: Query[Basics] = props.boolean neq sample.boolean orderBy (props.int.asc)
    repo.retrieveByQuery(query).failed.futureValue shouldBe an[OrderByInQueryException]
  }

}
