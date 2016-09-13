package longevity.test

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.LazyLogging
import emblem.TypeKey
import longevity.context.LongevityContext
import longevity.persistence.PState
import longevity.persistence.RepoPool
import longevity.persistence.inmem.InMemRepo
import longevity.subdomain.persistent.Persistent
import longevity.subdomain.ptype.Prop
import longevity.subdomain.ptype.Query
import org.scalatest.BeforeAndAfterAll
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.concurrent.ScaledTimeSpans
import org.scalatest.time.SpanSugar.convertIntToGrainOfTime
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/** contains common code for testing different [[longevity.subdomain.ptype.Query
 * Query]] instances against [[longevity.persistence.Repo.retrieveByQuery
 * Repo.retrieveByQuery]] and [[longevity.persistence.StreamingRepo.streamByQuery
 * Repo.streamByQuery]].
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
with LazyLogging
with Matchers
with ScalaFutures
with ScaledTimeSpans {

  override implicit def patienceConfig = PatienceConfig(
    timeout = scaled(4000.millis),
    interval = scaled(50.millis))

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
    val realizedProp = repo.realizedPType.realizedProps(prop)
    implicit val ordering = realizedProp.ordering
    entities.view.map(root => realizedProp.propVal(root)).toSeq.sorted.apply(k)
  }

  /** runs the query against the test data, and checks if the results are correct.
   * generates a test failure if they are not.
   */
  protected def exerciseQuery(query: Query[P], exerciseStreamByQuery: Boolean = false): Unit = {
    val results: Set[P] = repo.retrieveByQuery(query).futureValue.map(_.get).toSet
    val actual = pStates.map(_.get).toSet intersect results // remove any entities not put in by this test
    val expected = entitiesMatchingQuery(query, entities)

    if (actual != expected) {
      logger.debug(s"failure for query ${query}")
      logger.debug(s"  exerciseQuery actual = $actual")
      logger.debug(s"  exerciseQuery expected = $expected")
      logger.debug(s"  exerciseQuery extras = ${actual -- expected}")
    }
    actual.size should equal (expected.size)
    actual should equal (expected)

    if (exerciseStreamByQuery) exerciseStream(query, actual)
  }

  private def exerciseStream(query: Query[P], expected: Set[P]): Unit = {
    implicit val system = ActorSystem("QuerySpec")
    implicit val materializer = ActorMaterializer()
    val source = repo.streamByQuery(query)
    val results = source.runFold(Set.empty[PState[P]])(_ + _).futureValue.map(_.get)
    val actual = pStates.map(_.get).toSet intersect results

    if (actual != expected) {
      logger.debug(s"failure for query ${query}")
      logger.debug(s"  exerciseStream actual = $actual")
      logger.debug(s"  exerciseStream expected = $expected")
      logger.debug(s"  exerciseStream extras = ${actual -- expected}")
    }
    actual.size should equal (expected.size)
    actual should equal (expected)
  }

  private def generateP(): P = longevityContext.testDataGenerator.generate[P]

  private def entitiesMatchingQuery(query: Query[P], entities: Set[P]): Set[P] = {
    entities.filter(InMemRepo.queryMatches(query, _, repo.realizedPType))
  }

}
