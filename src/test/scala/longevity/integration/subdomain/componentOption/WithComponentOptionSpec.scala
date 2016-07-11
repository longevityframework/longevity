package longevity.integration.subdomain.componentOption

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class WithComponentOptionSpec extends Suites(
  mongoContext.inMemRepoCrudSpec,
  mongoContext.repoCrudSpec,
  cassandraContext.repoCrudSpec)
