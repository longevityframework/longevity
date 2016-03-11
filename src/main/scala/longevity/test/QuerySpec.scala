package longevity.test

import emblem.imports._
import longevity.context.LongevityContext
import longevity.persistence._
import longevity.subdomain._
import longevity.subdomain.ptype._
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.concurrent.ScaledTimeSpans
import org.scalatest.time.SpanSugar._
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.util.Random

/** contains common code for testing different [[longevity.subdomain.ptype.Query]]
 * instances against [[longevity.persistence.Repo#retrieveByQuery]]
 *
 * pardon the nasty ScalaDocs for this class. we haven't figured out how to
 * remove the methods inherited from ScalaTest classes yet.
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
abstract class QuerySpec[P <: Persistent : TypeKey](
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

  /** the number of entities to run queries against */
  protected val numEntities = 10

  /** the persistent type */
  protected final val pType = longevityContext.subdomain.pTypePool[P]

  /** the repository under test */
  protected final val repo = repoPool.baseRepoMap[P]

  /** the entities we are querying against */
  protected final var entities: Set[P] = _

  /** the persistent states of the entities we are querying against */
  protected final var pStates: Seq[PState[P]] = _

  override def beforeAll(): Unit = {
    val rootStateSeq = for (i <- 0.until(numEntities)) yield repo.create(generateP())
    pStates = Future.sequence(rootStateSeq).futureValue
    entities = pStates.map(_.get).toSet
  }

  private def generateP(): P = {
    val p = testDataGenerator.generate[P]
    repo.patchUnpersistedAssocs(p, CreatedCache()).futureValue._1
  }

  override def afterAll(): Unit = {
    Future.traverse(pStates)(rootState => repo.delete(rootState)).futureValue
  }

  /** pick an entity from the test set "at random". actually uses `Set.head` */
  protected def randomP: P = entities.head

  /** pick the entity with the median value for the provided property
   * @param prop the property to select the median value for
   */
  protected def medianPropVal[A](prop: Prop[P, A]): A = orderStatPropVal(prop, entities.size / 2)

  /** pick the entity with the specified order statistic for the provided
   * property. an order statistic `k` is the element indexed by `k` if the set
   * of entities were sorted by the supplied property.
   * 
   * @param prop the property to select the order statistic for
   * @param k the order statistic to select
   */
  protected def orderStatPropVal[A](prop: Prop[P, A], k: Int): A = {
    implicit val ordering = prop.ordering
    entities.view.map(root => prop.propVal(root)).toSeq.sorted.apply(k)
  }

  /** runs the query against the test data, and checks if the results are correct.
   * generates a test failure if they are not.
   */
  protected def exerciseQuery(query: Query[P]): Unit = {
    val results = repo.retrieveByQuery(query).futureValue.map(_.get).toSet
    val actual = entities intersect results // remove any entities not put in by this test
    val expected = entitiesMatchingQuery(query, entities)

    if (actual != expected) {
      println(s"failure for query ${query}")
    }
    actual.size should equal (expected.size)
    actual should equal (expected)
  }

  private def entitiesMatchingQuery(query: Query[P], entities: Set[P]): Set[P] = {
    entities.filter(InMemRepo.queryMatches(query, _))
  }

}
