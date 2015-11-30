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
abstract class QuerySpec(context: LongevityContext, pool: RepoPool)
extends {
  protected val longevityContext = context
  protected val repoPool = pool
}
with FlatSpec
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

  sealed trait QTemplate[R <: RootEntity]

  case class EqualityQTemplate[R <: RootEntity, A](prop: Prop[R, A]) extends QTemplate[R]

  case class OrderingQTemplate[R <: RootEntity, A](prop: Prop[R, A]) extends QTemplate[R]

  case class ConditionalQTemplate[R <: RootEntity](lhs: QTemplate[R], rhs: QTemplate[R])
  extends QTemplate[R]

  // TODO speed up these tests. figure out where they are so slow
  protected def exerciseQTemplate[R <: RootEntity : TypeKey](
    template: QTemplate[R],
    maxQueries: Int = 10)
  : Unit = {
    val rootStates = generateRoots(100)
    val roots = rootStates.map(_.get)
    try {
      val expectations = queryExpectationsFromQTemplate(template, roots)
      val expectationsSubset = expectations.take(maxQueries)
      expectationsSubset.foreach(e => exerciseQueryExpectations(e, roots))
    } finally {
      deleteRoots(rootStates)
    }
  }

  private def generateRoots[R <: RootEntity : TypeKey](count: Int): Seq[PState[R]] = {
    val repo = repoPool[R]
    val fpStates = for (i <- 0.until(count)) yield repo.create(testDataGenerator.generate[R])
    Future.sequence(fpStates).futureValue
  }

  private case class QueryExpectations[R <: RootEntity](query: Query[R], expected: Seq[R])

  private def queryExpectationsFromQTemplate[R <: RootEntity](
    template: QTemplate[R],
    roots: Seq[R])
  : Set[QueryExpectations[R]] = {
    template match {
      case t: EqualityQTemplate[R, _] => queryExpectationsFromEqualityQTemplate(t, roots)(t.prop.typeKey)
      case t: OrderingQTemplate[R, _] => queryExpectationsFromOrderingQTemplate(t, roots)(t.prop.typeKey)
      case t: ConditionalQTemplate[R] => queryExpectationsFromConditionalQTemplate(t, roots)
    }
  }

  private def queryExpectationsFromEqualityQTemplate[R <: RootEntity, A : TypeKey](
    template: EqualityQTemplate[R, A],
    roots: Seq[R])
  : Set[QueryExpectations[R]] = {
    val randomMatchingRoot = randomRoot(roots)
    val matchingPropVal = template.prop.propVal(randomMatchingRoot)
    val matchingRoots1 = roots.filter(r => template.prop.propVal(r) == matchingPropVal)
    val eqsWithMatch = QueryExpectations(Query.eqs(template.prop, matchingPropVal), matchingRoots1)
    val neqWithMatch = QueryExpectations(Query.neq(template.prop, matchingPropVal), roots diff matchingRoots1)

    val nonMatchingPropVal = testDataGenerator.generate[A]
    val matchingRoots2 = roots.filter(r => template.prop.propVal(r) == nonMatchingPropVal)

    val eqsNoMatch = QueryExpectations(Query.eqs(template.prop, nonMatchingPropVal), matchingRoots2)
    val neqNoMatch = QueryExpectations(Query.neq(template.prop, nonMatchingPropVal), roots diff matchingRoots2)

    Set(eqsWithMatch, neqWithMatch, eqsNoMatch, neqNoMatch)
  }

  private def randomRoot[R <: RootEntity](roots: Seq[R]): R = roots(math.abs(random.nextInt) % roots.size)

  private def queryExpectationsFromOrderingQTemplate[R <: RootEntity, A : TypeKey](
    template: OrderingQTemplate[R, A],
    roots: Seq[R])
  : Set[QueryExpectations[R]] = {
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

  private def queryExpectationsFromConditionalQTemplate[R <: RootEntity, A](
    template: ConditionalQTemplate[R],
    roots: Seq[R])
  : Set[QueryExpectations[R]] = {
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

  private def medianPropVal[R <: RootEntity, A](roots: Seq[R], prop: Prop[R, A]): A =
    orderStatPropVal(roots, prop, roots.size / 2)

  private def orderStatPropVal[R <: RootEntity, A](roots: Seq[R], prop: Prop[R, A], k: Int): A = {
    implicit val ordering = prop.ordering
    roots.map(root => prop.propVal(root)).sorted.apply(k)
  }

  private def exerciseQueryExpectations[R <: RootEntity : TypeKey](
    expectations: QueryExpectations[R],
    roots: Seq[R]): Unit = {
    val results = repoPool[R].retrieveByQuery(expectations.query).futureValue.map(_.get)
    val actual = roots intersect results // remove any roots not put in by this test
    actual.size should equal (expectations.expected.toSet.size)
    actual.toSet should equal (expectations.expected.toSet)
  }

  private def deleteRoots[R <: RootEntity : TypeKey](rootStates: Seq[PState[R]]): Unit = {
    val repo = repoPool[R]
    val fpStates = for (rootState <- rootStates) yield repo.delete(rootState.asInstanceOf[Persisted[R]])
    Future.sequence(fpStates).futureValue
  }

}
