package longevity.integration.noTranslation

import longevity.IntegrationTest
import longevity.MasterIntegrationTest
import longevity.exceptions.CouldNotTranslateException
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.concurrent.ScaledTimeSpans
import org.scalatest.time.SpanSugar._

@IntegrationTest
@MasterIntegrationTest
class NoTranslationSpec extends FlatSpec with GivenWhenThen with Matchers with ScalaFutures with ScaledTimeSpans {

  override implicit def patienceConfig = PatienceConfig(
    timeout = scaled(1000 millis),
    interval = scaled(50 millis))

  val repoPool = longevityContext.repoPool

  behavior of "Repo.create in the face of a untranslatable objects"

  it should "fail with CouldNotTranslateException for untranslatable objects embedded directly in root" in {
    val ps = repoPool[WithNoTranslation].create(
      WithNoTranslation("uri", NoTranslation("name")))
    ps.failed.futureValue shouldBe a [CouldNotTranslateException]
  }

  it should "fail with CouldNotTranslateException for list of untranslatable objects" in {
    val ps = repoPool[WithNoTranslationList].create(
      WithNoTranslationList("uri", NoTranslation("name1") :: NoTranslation("name2") :: Nil))
    ps.failed.futureValue shouldBe a [CouldNotTranslateException]
  }

  // TODO pt-99755864 the fact that an empty list doesnt fail really speaks to the fact that we have to fail
  // much earlier on malformed subdomains

  it should "not fail for an empty list of untranslatable objects" in {
    val ps = repoPool[WithNoTranslationList].create(
      WithNoTranslationList("uri", Nil))
    ps.futureValue shouldBe a [longevity.persistence.Persisted[_]]
  }

  it should "fail with CouldNotTranslateException for option of untranslatable objects" in {
    val ps = repoPool[WithNoTranslationOption].create(
      WithNoTranslationOption("uri", Option(NoTranslation("name1"))))
    ps.failed.futureValue shouldBe a [CouldNotTranslateException]
  }

  it should "not fail for an empty option of untranslatable objects" in {
    val ps = repoPool[WithNoTranslationOption].create(WithNoTranslationOption("uri", None))
    ps.futureValue shouldBe a [longevity.persistence.Persisted[_]]
  }

  it should "fail with CouldNotTranslateException for set of untranslatable objects" in {
    val ps = repoPool[WithNoTranslationSet].create(
      WithNoTranslationSet("uri", Set(NoTranslation("name1"), NoTranslation("name2"))))
    ps.failed.futureValue shouldBe a [CouldNotTranslateException]
  }

  it should "not fail for an empty set of untranslatable objects" in {
    val ps = repoPool[WithNoTranslationSet].create(WithNoTranslationSet("uri", Set()))
    ps.futureValue shouldBe a [longevity.persistence.Persisted[_]]
  }

  it should "fail with CouldNotTranslateException for longhand of untranslatable objects" in {
    val ps = repoPool[WithNoTranslationLonghand].create(
      WithNoTranslationLonghand("uri", NoTranslationLonghand(NoTranslation("name"))))
    ps.failed.futureValue shouldBe a [CouldNotTranslateException]
  }

}
