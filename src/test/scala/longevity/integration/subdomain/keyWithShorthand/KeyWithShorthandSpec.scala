package longevity.integration.subdomain.keyWithShorthand

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class KeyWithShorthandSpec extends Suites(
  mongoContext.inMemRepoCrudSpec,
  mongoContext.repoCrudSpec,
  cassandraContext.repoCrudSpec)

