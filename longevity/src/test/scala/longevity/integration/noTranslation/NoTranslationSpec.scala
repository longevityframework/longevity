package longevity.integration.noTranslation

import com.typesafe.scalalogging.LazyLogging
import longevity.exceptions.persistence.NotInSubdomainTranslationException
import longevity.persistence.RepoPool
import longevity.test.LongevityFuturesSpec
import org.scalatest.BeforeAndAfterAll
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import scala.concurrent.ExecutionContext.{ global => globalExecutionContext }

/** integration tests for things in the subdomain that don't have mongo
 * transations. this indicates a "bug" in the subdomain - some kind of shorthand
 * or entity is missing. this should really be tested for you on subdomain
 * construction.
 * 
 * @see https://www.pivotaltracker.com/story/show/99755864
 */
class NoTranslationSpec(val repoPool: RepoPool)
extends FlatSpec
with LongevityFuturesSpec
with BeforeAndAfterAll
with GivenWhenThen
with LazyLogging {

  override protected implicit val executionContext = globalExecutionContext

  override def beforeAll = repoPool.createSchema().recover({
    case t: Throwable =>
      logger.error("failed to create schema", t)
      throw t
  }).futureValue

  behavior of "Repo.create in the face of a untranslatable objects"

  it should "fail for untranslatable objects embedded directly in root" in {
    val ps = repoPool[WithNoTranslation].create(WithNoTranslation("uri", NoTranslation("name")))
    ps.failed.futureValue shouldBe a [NotInSubdomainTranslationException]
  }

  it should "fail for list of untranslatable objects" in {
    val ps = repoPool[WithNoTranslationList].create(
      WithNoTranslationList("uri", NoTranslation("name1") :: NoTranslation("name2") :: Nil))
    ps.failed.futureValue shouldBe a [NotInSubdomainTranslationException]
  }

  // TODO pt-99755864 the fact that an empty list doesnt fail really speaks to the fact that we have to fail
  // much earlier on malformed subdomains

  it should "not fail for an empty list of untranslatable objects" in {
    val ps = repoPool[WithNoTranslationList].create(
      WithNoTranslationList("uri", Nil))
    ps.futureValue
  }

  it should "fail for option of untranslatable objects" in {
    val ps = repoPool[WithNoTranslationOption].create(
      WithNoTranslationOption("uri", Option(NoTranslation("name1"))))
    ps.failed.futureValue shouldBe a [NotInSubdomainTranslationException]
  }

  it should "not fail for an empty option of untranslatable objects" in {
    val ps = repoPool[WithNoTranslationOption].create(WithNoTranslationOption("uri", None))
    ps.futureValue
  }

  it should "fail for set of untranslatable objects" in {
    val ps = repoPool[WithNoTranslationSet].create(
      WithNoTranslationSet("uri", Set(NoTranslation("name1"), NoTranslation("name2"))))
    ps.failed.futureValue shouldBe a [NotInSubdomainTranslationException]
  }

  it should "not fail for an empty set of untranslatable objects" in {
    val ps = repoPool[WithNoTranslationSet].create(WithNoTranslationSet("uri", Set()))
    ps.futureValue
  }

  it should "fail for longhand of untranslatable objects" in {
    val ps = repoPool[WithNoTranslationLonghand].create(
      WithNoTranslationLonghand("uri", NoTranslationLonghand(NoTranslation("name"))))
    ps.failed.futureValue shouldBe a [NotInSubdomainTranslationException]
  }

}
