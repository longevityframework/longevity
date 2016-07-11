package longevity.integration.subdomain.componentWithOption

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class WithComponentWithOptionSpec extends Suites(
  mongoContext.inMemRepoCrudSpec,
  mongoContext.repoCrudSpec,
  cassandraContext.repoCrudSpec)

