package longevity.test

import emblem.imports._
import longevity.context.LongevityContext
import longevity.persistence._
import longevity.subdomain._
import longevity.subdomain.root._
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.concurrent.ScaledTimeSpans
import org.scalatest.time.SpanSugar._
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.util.Random

/** contains common code for testing different [[longevity.subdomain.root.Query]]
 * instances against [[longevity.persistence.Repo#retrieveByQuery]]
 *
 * @param context the longevity context under test
 * 
 * @param repoPool the repo pool under test. this may be different than the
 * `context.repoPool`, as users may want to test against other repo pools. (for
 * instance, they may want a spec for in-memory repo pools if other parts of
 * their test suite rely on them.)
 * 
 * @param executionContext the execution context
 */
abstract class QuerySpec[R <: Root : TypeKey](
  context: LongevityContext,
  pool: RepoPool)(
  implicit executionContext: ExecutionContext)
extends {
  protected val longevityContext = context
  protected val repoPool = pool
}
with FlatSpec
with BeforeAndAfterAll
with GivenWhenThen
with Matchers
with ScalaFutures
with ScaledTimeSpans
with TestDataGeneration {

  override implicit def patienceConfig = PatienceConfig(
    timeout = scaled(4000 millis),
    interval = scaled(50 millis))

  /** the number of roots to run queries against */
  protected val numRoots = 10

  /** the root type */
  protected final val rootType = longevityContext.subdomain.rootTypePool[R]

  /** the repository under test */
  protected final val repo = repoPool.baseRepoMap[R]

  /** the roots we are querying against */
  protected final var roots: Set[R] = _

  /** the persistent state of the roots we are querying against */
  protected final var rootStates: Seq[PState[R]] = _

  override def beforeAll(): Unit = {
    val rootStateSeq = for (i <- 0.until(numRoots)) yield repo.create(generateRoot())
    rootStates = Future.sequence(rootStateSeq).futureValue
    roots = rootStates.map(_.get).toSet
  }

  private def generateRoot(): R = {
    val root: R = testDataGenerator.generate[R]
    repo.patchUnpersistedAssocs(root, CreatedCache()).futureValue._1
  }

  override def afterAll(): Unit = {
    Future.traverse(rootStates)(rootState => repo.delete(rootState)).futureValue
  }

  /** pick a root from the test set "at random". actually uses `Set.head` */
  protected def randomRoot: R = roots.head

  /** pick the root with the median value for the provided property
   * @param prop the property to select the median value for
   */
  protected def medianPropVal[A](prop: Prop[R, A]): A = orderStatPropVal(prop, roots.size / 2)

  /** pick the root with the specified order statistic for the provided
   * property. an order statistic `k` is the element indexed by `k` if the set
   * of roots were sorted by the supplied property.
   * 
   * @param prop the property to select the order statistic for
   * @param k the order statistic to select
   */
  protected def orderStatPropVal[A](prop: Prop[R, A], k: Int): A = {
    implicit val ordering = prop.ordering
    roots.view.map(root => prop.propVal(root)).toSeq.sorted.apply(k)
  }

  /** runs the query against the test data, and checks if the results are correct.
   * generates a test failure if they are not.
   */
  protected def exerciseQuery(query: Query[R]): Unit = {
    val results = repo.retrieveByQuery(query).futureValue.map(_.get).toSet
    val actual = roots intersect results // remove any roots not put in by this test
    val expected = rootsMatchingQuery(query, roots)

    if (actual != expected) {
      println(s"failure for query ${query}")
    }
    actual.size should equal (expected.size)
    actual should equal (expected)
  }

  private def rootsMatchingQuery(query: Query[R], roots: Set[R]): Set[R] = {
    val validatedQuery = rootType.validateQuery(query)
    roots.filter(InMemRepo.queryMatches(validatedQuery, _))
  }

}
