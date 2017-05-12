package longevity.test

import com.typesafe.scalalogging.LazyLogging
import longevity.context.LongevityContext
import org.scalatest.BeforeAndAfterAll
import org.scalatest.Suite

/** common code for longevity specs that use a longevity context with the test repo
 * 
 * @tparam M the model
 */
trait LongevityIntegrationSpec[M] extends LongevityFuturesSpec with BeforeAndAfterAll with LazyLogging {
  self: Suite =>

  protected val longevityContext: LongevityContext[M]

  override def beforeAll = longevityContext.testRepo.createSchema().recover({
    case t: Throwable =>
      logger.error("failed to create schema", t)
      throw t
  }).futureValue

  override def afterAll = longevityContext.testRepo.closeSession().futureValue

}
