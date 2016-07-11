package longevity.integration.subdomain.shorthandOptions

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class ShorthandOptionsSpec extends Suites(
  mongoContext.inMemRepoCrudSpec,
  mongoContext.repoCrudSpec,
  cassandraContext.repoCrudSpec)
