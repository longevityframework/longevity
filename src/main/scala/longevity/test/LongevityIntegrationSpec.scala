package longevity.test

import com.typesafe.scalalogging.LazyLogging
import longevity.context.LongevityContext
import longevity.persistence.RepoPool
import org.scalatest.BeforeAndAfterAll
import org.scalatest.Suite

/** common code for longevity specs that use a longevity context and a repo pool */
trait LongevityIntegrationSpec extends LongevityFuturesSpec with BeforeAndAfterAll with LazyLogging {
  self: Suite =>

  // TODO dont think i need repoPool here any more
  protected val longevityContext: LongevityContext
  protected val repoPool: RepoPool

  override def beforeAll = repoPool.createSchema().recover({
    case t: Throwable =>
      logger.error("failed to create schema", t)
      throw t
  }).futureValue

}
