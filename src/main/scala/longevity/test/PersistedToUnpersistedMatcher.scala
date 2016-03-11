package longevity.test

import emblem.TypeBoundPair
import emblem.imports._
import emblem.traversors.sync.CustomGenerator
import emblem.traversors.sync.CustomGeneratorPool
import emblem.traversors.sync.Differ
import emblem.traversors.sync.Generator
import emblem.traversors.sync.TestDataGenerator
import longevity.context.LongevityContext
import longevity.persistence.RepoPool
import longevity.subdomain._
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.concurrent.ScaledTimeSpans
import org.scalatest.time.SpanSugar._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/** a mixin trait for comparing persisted and unpersisted variations of an aggregate */
trait PersistedToUnpersistedMatcher extends Suite with ScalaFutures {

  protected val longevityContext: LongevityContext
  protected val repoPool: RepoPool

  override implicit def patienceConfig = PatienceConfig(
    timeout = scaled(4000 millis),
    interval = scaled(50 millis))

  private val subdomain = longevityContext.subdomain
  private val emblemPool = subdomain.entityEmblemPool
  private val extractorPool = shorthandPoolToExtractorPool(subdomain.shorthandPool)
  private val unpersistor = new PersistedToUnpersistedTransformer(emblemPool, extractorPool, repoPool)
  private val differ = new Differ(emblemPool, extractorPool)

  protected def persistedShouldMatchUnpersisted[P <: Persistent : TypeKey](
    persisted: P, unpersisted: P)
  : Unit = {
    val unpersistorated = unpersistor.transform(Future(persisted))
    if (unpersistorated.futureValue != unpersisted) {
      val diffs = differ.diff(unpersistorated, unpersisted)
      fail (Differ.explainDiffs(diffs, true))
    }
  }
 
}
