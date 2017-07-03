package longevity.integration.noTranslation

import com.typesafe.scalalogging.LazyLogging
import longevity.exceptions.persistence.NotInDomainModelTranslationException
import longevity.effect.Effect
import longevity.persistence.Repo
import org.scalatest.BeforeAndAfterAll
import org.scalatest.FlatSpec
import org.scalatest.Matchers
import scala.util.control.NonFatal

/** integration tests for things in the domain model that don't have mongo
 * transations. this indicates a "bug" in the domain model - some kind of shorthand
 * or entity is missing. this should really be tested for you on domain model
 * construction.
 * 
 * @see https://www.pivotaltracker.com/story/show/99755864
 */
class NoTranslationSpec[F[_]](val effect: Effect[F], val repo: Repo[F, DomainModel])
extends FlatSpec with Matchers with BeforeAndAfterAll with LazyLogging {

  override def beforeAll = try {
    effect.run(repo.createSchema)
  } catch {
    case NonFatal(e) =>
      logger.error("failed to create schema", e)
      throw e
  }

  behavior of "Repo.create in the face of a untranslatable objects"

  it should "fail for untranslatable objects embedded directly in root" in {
    val ps = repo.create(WithNoTranslation("uri", NoTranslation("name")))
    intercept [NotInDomainModelTranslationException] {
      effect.run(ps)
    }
  }

  it should "fail for list of untranslatable objects" in {
    val ps = repo.create(
      WithNoTranslationList("uri", NoTranslation("name1") :: NoTranslation("name2") :: Nil))
    intercept [NotInDomainModelTranslationException] {
      effect.run(ps)
    }
  }

  // TODO pt-99755864 the fact that an empty list doesnt fail really speaks to the fact that we have to fail
  // much earlier on malformed domain models

  it should "not fail for an empty list of untranslatable objects" in {
    val ps = repo.create(WithNoTranslationList("uri", Nil))
    effect.run(ps)
  }

  it should "fail for option of untranslatable objects" in {
    val ps = repo.create(WithNoTranslationOption("uri", Option(NoTranslation("name1"))))
    intercept [NotInDomainModelTranslationException] {
      effect.run(ps)
    }
  }

  it should "not fail for an empty option of untranslatable objects" in {
    val ps = repo.create(WithNoTranslationOption("uri", None))
    effect.run(ps)
  }

  it should "fail for set of untranslatable objects" in {
    val ps = repo.create(WithNoTranslationSet("uri", Set(NoTranslation("name1"), NoTranslation("name2"))))
    intercept [NotInDomainModelTranslationException] {
      effect.run(ps)
    }
  }

  it should "not fail for an empty set of untranslatable objects" in {
    val ps = repo.create(WithNoTranslationSet("uri", Set()))
    effect.run(ps)
  }

  it should "fail for longhand of untranslatable objects" in {
    val ps = repo.create(WithNoTranslationLonghand("uri", NoTranslationLonghand(NoTranslation("name"))))
    intercept [NotInDomainModelTranslationException] {
      effect.run(ps)
    }
  }

}
