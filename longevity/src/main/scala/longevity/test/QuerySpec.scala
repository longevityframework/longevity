package longevity.test

import com.typesafe.scalalogging.LazyLogging
import emblem.TypeKey
import longevity.context.LongevityContext
import longevity.model.ptype.Prop
import longevity.model.query.Query
import longevity.model.query.QueryFilter
import longevity.model.query.QueryOrderBy
import longevity.persistence.PState
import longevity.persistence.RepoPool
import org.scalatest.FlatSpec
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/** contains common code for testing different [[longevity.model.query.Query Query]] instances
 * against [[longevity.persistence.OldRepo.queryToFutureVec Repo.queryToFutureVec]],
 * [[longevity.persistence.OldRepo.queryToIterator]], and the four streaming query libraries:
 * 
 *   - [[longevity.persistence.streams.AkkaStreamsRepo.queryToAkkaStream AkkaStreamsRepo.queryToAkkaStream]]
 *   - [[longevity.persistence.streams.FS2Repo.queryToFS2 FS2Repo.queryToFS2]]
 *   - [[longevity.persistence.streams.IterateeIoRepo.queryToIterateeIo IterateeIoRepo.queryToIterateeIo]]
 *   - [[longevity.persistence.streams.PlayRepo.queryToPlay PlayRepo.queryToPlay]]
 *
 * pardon the nasty ScalaDocs for this class. we haven't figured out how to
 * remove the methods inherited from ScalaTest classes yet.
 *
 * @param context the longevity context under test
 * 
 * @param executionContext the execution context
 */
abstract class QuerySpec[P : TypeKey](
  protected val longevityContext: LongevityContext)(
  protected implicit val executionContext: ExecutionContext)
extends FlatSpec with LongevityIntegrationSpec with LazyLogging {

  protected val repoPool: RepoPool = longevityContext.testRepoPool

  /** the number of entities to run queries against */
  protected val numEntities = 10

  /** the persistent type */
  protected final val pType = longevityContext.domainModel.pTypePool[P]

  /** the repository under test */
  protected final val repo = repoPool.baseRepoMap[P]

  /** the entities we are querying against */
  protected final var entities: Set[P] = _

  /** the persistent states of the entities we are querying against */
  protected final var pStates: Seq[PState[P]] = _

  private val realizedPType = longevityContext.domainModel.realizedPTypes(pType)

  override def beforeAll(): Unit = {
    super.beforeAll()
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
    val realizedProp = realizedPType.realizedProps(prop)
    implicit val ordering = realizedProp.ordering
    entities.view.map(root => realizedProp.propVal(root)).toSeq.sorted.apply(k)
  }

  /** runs the query against the test data, and checks if the results are correct.
   * generates a test failure if they are not.
   *
   * due to the possibility of data in the table put in by other tests, it is not
   * possible to test queries with `offset` or `limit` clauses here. do not despair,
   * offset and limit clauses are tested independently in the longevity test suite
   * for every longevity back end. (see
   * `longevity.integration.queries.offsetLimit.OffsetLimitQuerySpec`)
   */
  protected def exerciseQuery(query: Query[P], exerciseQueryToStreams: Boolean = false): Unit = {
    if (query.offset.nonEmpty || query.limit.nonEmpty) {
      fail("QuerySpec.exerciseQuery cannot be used to test queries with offset and limit clauses")
    }

    val orderedResults = repo.queryToFutureVec(query).futureValue.map(_.get)
    val results: Set[P] = orderedResults.toSet
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

    if (query.orderBy.sortExprs.nonEmpty && orderedResults.size > 1) {
      val ordering = QueryOrderBy.ordering(query.orderBy, realizedPType)
      orderedResults.sliding(2).foreach { consecutive =>
        ordering.compare(consecutive(0), consecutive(1)) should be <= 0
      }
    }

    if (exerciseQueryToStreams) exerciseStreams(query, actual)
  }

  // to be overridden by traits that exercise specific streaming back ends
  protected def exerciseStreams(query: Query[P], expected: Set[P]): Unit = {
    exerciseToIterator(query, expected)
  }

  private def exerciseToIterator(query: Query[P], expected: Set[P]): Unit = {
    val S = fs2.Strategy.fromFixedDaemonPool(8, threadName = "worker")
    val source = repo.queryToIterator(query)
    val results = source.map(_.get).toSet
    val actual = pStates.map(_.get).toSet intersect results
    exerciseStream(query, actual, expected)
  }

  protected def exerciseStream(query: Query[P], actual: Set[P], expected: Set[P]): Unit = {
    if (actual != expected) {
      logger.debug(s"failure for query ${query}")
      logger.debug(s"  exerciseStream actual = $actual")
      logger.debug(s"  exerciseStream expected = $expected")
      logger.debug(s"  exerciseStream extras = ${actual -- expected}")
    }
    actual.size should equal (expected.size)
    actual should equal (expected)
  }

  protected def generateP(): P = longevityContext.testDataGenerator.generate[P]

  private def entitiesMatchingQuery(query: Query[P], entities: Set[P]): Set[P] = {
    entities.filter(QueryFilter.matches(query.filter, _, realizedPType))
  }

}
