package longevity.test

import journal.Logger
import longevity.context.LongevityContext
import org.scalatest.BeforeAndAfterAll
import org.scalatest.Matchers
import org.scalatest.Suite
import scala.util.control.NonFatal

/** common code for longevity specs that use a longevity context with the test repo
 *
 * @tparam F the effect
 * @tparam M the model
 */
trait LongevityIntegrationSpec[F[_], M] extends Matchers with BeforeAndAfterAll {
  self: Suite =>

  private val logger = Logger[this.type]

  protected val longevityContext: LongevityContext[F, M]

  override def beforeAll = try {
    longevityContext.effect.run(longevityContext.testRepo.createSchema)
  } catch {
    case NonFatal(e) =>
      logger.error("failed to create schema", e)
      throw e
  }

  override def afterAll = longevityContext.effect.run(longevityContext.testRepo.closeConnection)

}
