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
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Random

/** contains common code for testing different [[longevity.subdomain.root.Query]] instances against
 * [[longevity.persistence.Repo#retrieveByQuery]]
 *
 * @param context the longevity context under test
 * @param repoPool the repo pool under test. this may be different than the `context.repoPool`, as
 * users may want to test against other repo pools. (for instance, they may want a spec for in-memory repo
 * pools if other parts of their test suite rely on them.)
 */
abstract class QuerySpec[R <: RootEntity : TypeKey](context: LongevityContext, pool: RepoPool)
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

  // by default, things should be repeatable. but it would be nice if there were an easy way to tweak the seed
  val seed = 117
  val random = new Random(seed)

  override implicit def patienceConfig = PatienceConfig(
    timeout = scaled(4000 millis),
    interval = scaled(50 millis))

  protected sealed trait QTemplate
  protected case class EqualityQTemplate[A](prop: Prop[R, A]) extends QTemplate
  protected case class OrderingQTemplate[A](prop: Prop[R, A]) extends QTemplate
  protected case class ConditionalQTemplate(lhs: QTemplate, rhs: QTemplate) extends QTemplate

  protected def exerciseQTemplate(template: QTemplate, maxQueries: Int = 10): Unit = {
    val expectations = queryExpectationsFromQTemplate(template, roots)
    val expectationsSubset = expectations.take(maxQueries)
    expectationsSubset.foreach(e => exerciseQueryExpectations(e))
  }

  private var roots: Set[R] = _
  private var rootStates: Seq[PState[R]] = _

  override def beforeAll(): Unit = {
    val repo = repoPool[R]
    val rootStateSeq = for (i <- 0.until(100)) yield repo.create(testDataGenerator.generate[R])
    rootStates = Future.sequence(rootStateSeq).futureValue
    roots = rootStates.map(_.get).toSet
  }

  override def afterAll(): Unit = {
    val repo = repoPool[R]
    Future.traverse(rootStates)(rootState => repo.delete(rootState)).futureValue
  }

  private case class QueryExpectations(query: Query[R], expected: Set[R])

  private def queryExpectationsFromQTemplate(template: QTemplate, roots: Set[R]): Set[QueryExpectations] = {
    template match {
      case t: EqualityQTemplate[_] => queryExpectationsFromEqualityQTemplate(t, roots)(t.prop.typeKey)
      case t: OrderingQTemplate[_] => queryExpectationsFromOrderingQTemplate(t, roots)(t.prop.typeKey)
      case t: ConditionalQTemplate => queryExpectationsFromConditionalQTemplate(t, roots)
    }
  }

  private def queryExpectationsFromEqualityQTemplate[A : TypeKey](
    template: EqualityQTemplate[A],
    roots: Set[R])
  : Set[QueryExpectations] = {
    val randomMatchingRoot = randomRoot(roots)
    val matchingPropVal = template.prop.propVal(randomMatchingRoot)
    val matchingRoots = roots.filter(r => template.prop.propVal(r) == matchingPropVal)
    val eqsWithMatch = QueryExpectations(Query.eqs(template.prop, matchingPropVal), matchingRoots)
    val neqWithMatch = QueryExpectations(Query.neq(template.prop, matchingPropVal), roots diff matchingRoots)
    Set(eqsWithMatch, neqWithMatch)
  }

  private def randomRoot(roots: Set[R]): R = roots.head

  private def queryExpectationsFromOrderingQTemplate[A : TypeKey](
    template: OrderingQTemplate[A],
    roots: Set[R])
  : Set[QueryExpectations] = {
    val prop = template.prop
    val median = medianPropVal(roots, prop)

    val ltQuery = Query.lt(prop, median)
    val ltRoots = roots.filter(r => prop.ordering.lt(prop.propVal(r), median))
    
    val lteQuery = Query.lte(prop, median)
    val lteRoots = roots.filter(r => prop.ordering.lteq(prop.propVal(r), median))

    val gtQuery = Query.gt(prop, median)
    val gtRoots = roots.filter(r => prop.ordering.gt(prop.propVal(r), median))

    val gteQuery = Query.gte(prop, median)
    val gteRoots = roots.filter(r => prop.ordering.gteq(prop.propVal(r), median))

    Set(
      QueryExpectations(ltQuery, ltRoots),
      QueryExpectations(lteQuery, lteRoots),
      QueryExpectations(gtQuery, gtRoots),
      QueryExpectations(gteQuery, gteRoots))
  }

  private def queryExpectationsFromConditionalQTemplate[A](
    template: ConditionalQTemplate,
    roots: Set[R])
  : Set[QueryExpectations] = {
    val setSetExpects = for {
      lhsExpect <- queryExpectationsFromQTemplate(template.lhs, roots)
      rhsExpect <- queryExpectationsFromQTemplate(template.rhs, roots)
    } yield {
      Set(
        QueryExpectations(
          Query.and(lhsExpect.query, rhsExpect.query),
          lhsExpect.expected intersect rhsExpect.expected),
        QueryExpectations(
          Query.or(lhsExpect.query, rhsExpect.query),
          lhsExpect.expected union rhsExpect.expected))
    }
    setSetExpects.flatten
  }

  private def medianPropVal[A](roots: Set[R], prop: Prop[R, A]): A =
    orderStatPropVal(roots, prop, roots.size / 2)

  private def orderStatPropVal[A](roots: Set[R], prop: Prop[R, A], k: Int): A = {
    implicit val ordering = prop.ordering
    roots.view.map(root => prop.propVal(root)).toSeq.sorted.apply(k)
  }

  private def exerciseQueryExpectations(expectations: QueryExpectations): Unit = {
    val results = repoPool[R].retrieveByQuery(expectations.query).futureValue.map(_.get).toSet
    val actual = roots intersect results // remove any roots not put in by this test
    
    if (actual.size != expectations.expected.size) {
      println(s"failure for query ${expectations.query}")
      println(s"  expected ${expectations.expected}")
    }
    actual.size should equal (expectations.expected.size)
    actual should equal (expectations.expected)
  }

}
