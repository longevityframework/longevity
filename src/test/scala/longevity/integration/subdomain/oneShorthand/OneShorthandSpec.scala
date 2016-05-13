package longevity.integration.subdomain.oneShorthand

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class OneShorthandSpec extends Suites(
  mongoContext.inMemRepoCrudSpec,
  mongoContext.repoCrudSpec,
  cassandraContext.repoCrudSpec)

