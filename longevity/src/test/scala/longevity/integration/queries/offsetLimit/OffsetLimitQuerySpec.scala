package longevity.integration.queries.offsetLimit

import longevity.context.LongevityContext
import longevity.model.DomainModel
import longevity.model.PType
import longevity.model.PTypePool
import longevity.model.query.Query
import longevity.persistence.PState
import longevity.test.LongevityIntegrationSpec
import org.scalatest.FlatSpec
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/** we use a special domain model for limit/offset tests to prevent interference
 * from other tests.
 *
 * please note that if for some reason afterAll fails to run, then future runs
 * of this test will fail until someone manually cleans out (or deletes) the
 * limit_offset table.
 */
object OffsetLimitQuerySpec {

  case class OffsetLimit(i: Int, j: Int)

  object OffsetLimit extends PType[OffsetLimit] {
    object props {
      val i = prop[Int]("i")
      val j = prop[Int]("j")
    }
    val keySet = emptyKeySet
    override val indexSet = Set(index(props.i), index(props.j))
  }

  val domainModel = DomainModel(PTypePool(OffsetLimit))

}

/** abstract superclass for tests of query limit/offset clauses
 *
 * @param longevityContext the context to use. it must be built from
 * `OffsetLimitQuerySpec.domainModel`
 *
 * @param testOffsets if false, we avoid order by and offset clauses
 * in our tests. this is a concession to Cassandra back end. once we
 * implement partition indexes, we might want to rework this test to use
 * order by clause, which will allow for more precise results.
 *
 * @see https://www.pivotaltracker.com/story/show/127406611
 */
class OffsetLimitQuerySpec(
  protected val longevityContext: LongevityContext,
  private val testOffsets: Boolean = true)
extends FlatSpec with LongevityIntegrationSpec {

  protected implicit val executionContext = ExecutionContext.global

  import OffsetLimitQuerySpec._

  val ps = for (i <- 0 until 10) yield OffsetLimit(i, 0)
  val repo = longevityContext.testRepo
  
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

  behavior of "Repo.queryToFutureVec"

  it should "return correct results for queries with limit clauses" in {
    var query: Query[OffsetLimit] = null
    var results: Seq[PState[OffsetLimit]] = null

    query = props.j eqs 0 and props.i gt 1 limit 5
    results = repo.queryToFutureVec(query).futureValue
    results.size should equal (5)
    results should not contain (OffsetLimit(0, 0))
    results should not contain (OffsetLimit(1, 0))

    query = props.j eqs 0 and props.i gt 1 limit 50
    results = repo.queryToFutureVec(query).futureValue
    results.map(_.get.i).toSet should equal (Set(2, 3, 4, 5, 6, 7, 8, 9))
  }

  if (testOffsets) {

    it should "return correct results for queries with offset clauses" in {
      var query: Query[OffsetLimit] = null
      var results: Seq[PState[OffsetLimit]] = null

      query = props.i neq 3 orderBy props.i
      results = repo.queryToFutureVec(query).futureValue
      results.size should equal (9)
      results(0).get.i should equal (0)

      query = props.i neq 3 orderBy props.i offset 1
      results = repo.queryToFutureVec(query).futureValue
      results.size should equal (8)
      results(0).get.i should equal (1)

      query = props.i neq 3 orderBy props.i offset 9
      results = repo.queryToFutureVec(query).futureValue
      results.size should equal (0)

      query = props.i neq 3 orderBy props.i offset 10
      results = repo.queryToFutureVec(query).futureValue
      results.size should equal (0)

    }

    it should "return correct results for queries with limit and offset clauses" in {
      var query: Query[OffsetLimit] = null
      var results: Seq[PState[OffsetLimit]] = null

      query = props.i neq 3 orderBy props.i limit 5
      results = repo.queryToFutureVec(query).futureValue
      results.size should equal (5)
      results(0).get.i should equal (0)

      query = props.i neq 3 orderBy props.i limit 50
      results = repo.queryToFutureVec(query).futureValue
      results.size should equal (9)
      results(0).get.i should equal (0)

      query = props.i neq 3 orderBy props.i offset 1 limit 5
      results = repo.queryToFutureVec(query).futureValue
      results.size should equal (5)
      results(0).get.i should equal (1)

      query = props.i neq 3 orderBy props.i offset 1 limit 50
      results = repo.queryToFutureVec(query).futureValue
      results.size should equal (8)
      results(0).get.i should equal (1)

      query = props.i neq 3 orderBy props.i offset 10 limit 5
      results = repo.queryToFutureVec(query).futureValue
      results.size should equal (0)

    }
  }

}
