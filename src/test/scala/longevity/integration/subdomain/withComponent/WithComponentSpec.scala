package longevity.integration.subdomain.withComponent

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class WithComponentSpec extends Suites(
  mongoContext.inMemRepoCrudSpec,
  mongoContext.repoCrudSpec,
  cassandraContext.repoCrudSpec)

