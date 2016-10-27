package longevity.test

import com.typesafe.scalalogging.LazyLogging
import longevity.context.LongevityContext
import org.scalatest.BeforeAndAfterAll
import org.scalatest.Suite

/** common code for longevity specs that use a longevity context and a repo pool */
trait LongevityIntegrationSpec extends LongevityFuturesSpec with BeforeAndAfterAll with LazyLogging {
  self: Suite =>

  protected val longevityContext: LongevityContext

  override def beforeAll = longevityContext.testRepoPool.createSchema().recover({
    case t: Throwable =>
      logger.error("failed to create schema", t)
      throw t
  }).futureValue

  override def afterAll = longevityContext.testRepoPool.closeSession().futureValue

}
