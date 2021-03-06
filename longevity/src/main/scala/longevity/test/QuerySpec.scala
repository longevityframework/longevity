package longevity.test

import journal.Logger
import longevity.context.LongevityContext
import longevity.model.PEv
import longevity.model.ptype.Prop
import longevity.model.query.Query
import longevity.model.query.QueryFilter
import longevity.model.query.QueryOrderBy
import longevity.persistence.PState
import longevity.persistence.Repo
import org.scalatest.FlatSpec

/** contains common code for testing different [[longevity.model.query.Query Query]] instances
 * against [[longevity.persistence.Repo.queryToVector Repo.queryToVector]],
 * [[longevity.persistence.Repo.queryToIterator]], and the four streaming query libraries:
 * 
 *   - [[longevity.persistence.streams.AkkaStreamsRepo.queryToAkkaStream AkkaStreamsRepo.queryToAkkaStream]]
 *   - [[longevity.persistence.streams.FS2Repo.queryToFS2 FS2Repo.queryToFS2]]
 *   - [[longevity.persistence.streams.IterateeIoRepo.queryToIterateeIo IterateeIoRepo.queryToIterateeIo]]
 *   - [[longevity.persistence.streams.PlayRepo.queryToPlay PlayRepo.queryToPlay]]
 *
 * pardon the nasty ScalaDocs for this class. we haven't figured out how to
 * remove the methods inherited from ScalaTest classes yet.
 *
 * @tparam F the effect
 * @tparam M the model
 * @tparam P the persistent type
 * 
 * @param context the longevity context under test
 * @param pEv the persistent evidence
 * @param executionContext the execution context
 */
abstract class QuerySpec[F[_], M, P](
  protected val longevityContext: LongevityContext[F, M])(
  protected implicit val pEv: PEv[M, P])
extends FlatSpec with LongevityIntegrationSpec[F, M] {

  protected val logger = Logger[this.type]

  protected val repo: Repo[F, M] = longevityContext.testRepo

  protected val effect = longevityContext.effect

  /** the number of entities to run queries against */
  protected val numEntities = 10

  /** the persistent type */
  protected final val pType = longevityContext.modelType.pTypePool(pEv.key)

  /** the entities we are querying against */
  protected final var entities: Set[P] = _

  /** the persistent states of the entities we are querying against */
  protected final var pStates: Seq[PState[P]] = _

  private val realizedPType = longevityContext.modelType.realizedPTypes(pType)

  override def beforeAll(): Unit = {
    super.beforeAll()
    val createResultSeq = for (i <- 0.until(numEntities)) yield repo.create(generateP())
    pStates = createResultSeq.map(longevityContext.effect.run)
    entities = pStates.map(_.get).toSet
  }

  override def afterAll(): Unit = pStates.foreach { p => longevityContext.effect.run(repo.delete(p)) }

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

    val orderedResults = longevityContext.effect.run(repo.queryToVector(query)).map(_.get)
    val results: Set[P] = orderedResults.toSet
    val actual = pStates.map(_.get).toSet intersect results // remove any results not put in by this test
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
    val source = effect.run(repo.queryToIterator(query))
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

  protected def generateP(): P = longevityContext.testDataGenerator.generateP

  private def entitiesMatchingQuery(query: Query[P], entities: Set[P]): Set[P] = {
    entities.filter(QueryFilter.matches(query.filter, _, realizedPType))
  }

}
