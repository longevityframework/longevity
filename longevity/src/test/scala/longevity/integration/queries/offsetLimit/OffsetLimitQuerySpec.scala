package longevity.integration.queries.offsetLimit

import longevity.context.LongevityContext
import longevity.subdomain.query.Query
import longevity.persistence.PState
import longevity.test.LongevityIntegrationSpec
import org.scalatest.FlatSpec
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/** we use special subdomain for limit/offset tests to prevent interference
 * from other tests.
 *
 * please note that if for some reason afterAll fails to run, then future runs
 * of this test will fail until someone manually cleans out (or deletes) the
 * limit_offset table.
 */
object OffsetLimitQuerySpec {

  import longevity.subdomain.Persistent
  import longevity.subdomain.PType
  import longevity.subdomain.PTypePool
  import longevity.subdomain.Subdomain

  case class OffsetLimit(i: Int) extends Persistent

  object OffsetLimit extends PType[OffsetLimit] {
    object props {
      val i = prop[Int]("i")
    }
    object keys {
    }
  }

  val subdomain = Subdomain("limit offset test", PTypePool(OffsetLimit))

}

/** abstract superclass for tests of query limit/offset clauses */
class OffsetLimitQuerySpec(
  protected val longevityContext: LongevityContext)
extends FlatSpec with LongevityIntegrationSpec {

  protected implicit val executionContext = ExecutionContext.global

  import OffsetLimitQuerySpec._

  val ps = for (i <- 0 until 10) yield OffsetLimit(i)

  val repo = longevityContext.repoPool[OffsetLimit]
  var states: Seq[PState[OffsetLimit]] = _

  override def beforeAll = {
    super.beforeAll
    states = Future.sequence(ps.map(repo.create)).futureValue
  }

  override def afterAll = {
    Future.sequence(states.map(repo.delete)).futureValue
  }

  import OffsetLimit.queryDsl._
  import OffsetLimit.props

  behavior of "Repo.retrieveByQuery"

  it should "return correct results for queries with offset clauses" in {
    var query: Query[OffsetLimit] = null
    var results: Seq[PState[OffsetLimit]] = null

    query = props.i neq 3 orderBy props.i
    results = repo.retrieveByQuery(query).futureValue
    results.size should equal (9)
    results(0).get.i should equal (0)

    query = props.i neq 3 orderBy props.i offset 1
    results = repo.retrieveByQuery(query).futureValue
    results.size should equal (8)
    results(0).get.i should equal (1)

    query = props.i neq 3 orderBy props.i offset 9
    results = repo.retrieveByQuery(query).futureValue
    results.size should equal (0)

    query = props.i neq 3 orderBy props.i offset 10
    results = repo.retrieveByQuery(query).futureValue
    results.size should equal (0)

  }

  it should "return correct results for queries with limit clauses" in {
    var query: Query[OffsetLimit] = null
    var results: Seq[PState[OffsetLimit]] = null

    query = props.i neq 3 orderBy props.i limit 5
    results = repo.retrieveByQuery(query).futureValue
    results.size should equal (5)
    results(0).get.i should equal (0)

    query = props.i neq 3 orderBy props.i limit 50
    results = repo.retrieveByQuery(query).futureValue
    results.size should equal (9)
    results(0).get.i should equal (0)

    query = props.i neq 3 orderBy props.i offset 1 limit 5
    results = repo.retrieveByQuery(query).futureValue
    results.size should equal (5)
    results(0).get.i should equal (1)

    query = props.i neq 3 orderBy props.i offset 1 limit 50
    results = repo.retrieveByQuery(query).futureValue
    results.size should equal (8)
    results(0).get.i should equal (1)

    query = props.i neq 3 orderBy props.i offset 10 limit 5
    results = repo.retrieveByQuery(query).futureValue
    results.size should equal (0)

  }

}
