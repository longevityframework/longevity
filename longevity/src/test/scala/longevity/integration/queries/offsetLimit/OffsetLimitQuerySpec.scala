package longevity.integration.queries.offsetLimit

import longevity.context.LongevityContext
import longevity.model.query.Query
import longevity.persistence.PState
import longevity.test.LongevityIntegrationSpec
import org.scalatest.FlatSpec
import longevity.effect.Blocking

/** abstract superclass for tests of query limit/offset clauses
 *
 * @param longevityContext the context to use. it must be built from
 * `OffsetLimitQuerySpec.modelType`
 *
 * @param testOffsets if false, we avoid order by and offset clauses
 * in our tests. this is a concession to Cassandra back end. once we
 * implement partition indexes, we might want to rework this test to use
 * order by clause, which will allow for more precise results.
 *
 * @see https://www.pivotaltracker.com/story/show/127406611
 */
class OffsetLimitQuerySpec(
  protected val longevityContext: LongevityContext[Blocking, DomainModel],
  private val testOffsets: Boolean = true)
extends FlatSpec with LongevityIntegrationSpec[Blocking, DomainModel] {

  val ps: Seq[OffsetLimit] = for (i <- 0 until 10) yield OffsetLimit(i, 0)
  val repo = longevityContext.testRepo
  private val effect = longevityContext.effect
  
  var states: Seq[PState[OffsetLimit]] = _

  override def beforeAll = {
    super.beforeAll
    def create(p: OffsetLimit) = repo.create(p)
    states = ps.map(create).map(effect.run)
  }

  override def afterAll = {
    def delete(s: PState[OffsetLimit]) = repo.delete(s)
    states.map(delete).map(effect.run)
    super.afterAll
  }

  import OffsetLimit.queryDsl._
  import OffsetLimit.props

  behavior of "Repo.queryToVector"

  it should "return correct results for queries with limit clauses" in {
    var query: Query[OffsetLimit] = null
    var results: Seq[PState[OffsetLimit]] = null

    query = props.j eqs 0 and props.i gt 1 limit 5
    results = effect.run(repo.queryToVector(query))
    results.size should equal (5)
    results should not contain (OffsetLimit(0, 0))
    results should not contain (OffsetLimit(1, 0))

    query = props.j eqs 0 and props.i gt 1 limit 50
    results = effect.run(repo.queryToVector(query))
    results.map(_.get.i).toSet should equal (Set(2, 3, 4, 5, 6, 7, 8, 9))
  }

  if (testOffsets) {

    it should "return correct results for queries with offset clauses" in {
      var query: Query[OffsetLimit] = null
      var results: Seq[PState[OffsetLimit]] = null

      query = props.i neq 3 orderBy props.i
      results = effect.run(repo.queryToVector(query))
      results.size should equal (9)
      results(0).get.i should equal (0)

      query = props.i neq 3 orderBy props.i offset 1
      results = effect.run(repo.queryToVector(query))
      results.size should equal (8)
      results(0).get.i should equal (1)

      query = props.i neq 3 orderBy props.i offset 9
      results = effect.run(repo.queryToVector(query))
      results.size should equal (0)

      query = props.i neq 3 orderBy props.i offset 10
      results = effect.run(repo.queryToVector(query))
      results.size should equal (0)

    }

    it should "return correct results for queries with limit and offset clauses" in {
      var query: Query[OffsetLimit] = null
      var results: Seq[PState[OffsetLimit]] = null

      query = props.i neq 3 orderBy props.i limit 5
      results = effect.run(repo.queryToVector(query))
      results.size should equal (5)
      results(0).get.i should equal (0)

      query = props.i neq 3 orderBy props.i limit 50
      results = effect.run(repo.queryToVector(query))
      results.size should equal (9)
      results(0).get.i should equal (0)

      query = props.i neq 3 orderBy props.i offset 1 limit 5
      results = effect.run(repo.queryToVector(query))
      results.size should equal (5)
      results(0).get.i should equal (1)

      query = props.i neq 3 orderBy props.i offset 1 limit 50
      results = effect.run(repo.queryToVector(query))
      results.size should equal (8)
      results(0).get.i should equal (1)

      query = props.i neq 3 orderBy props.i offset 10 limit 5
      results = effect.run(repo.queryToVector(query))
      results.size should equal (0)

    }
  }

}
