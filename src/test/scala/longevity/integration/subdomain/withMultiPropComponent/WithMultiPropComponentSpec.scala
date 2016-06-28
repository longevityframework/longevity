package longevity.integration.subdomain.withMultiPropComponent

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class WithMultiPropComponentSpec extends Suites(
  mongoContext.inMemRepoCrudSpec,
  mongoContext.repoCrudSpec,
  cassandraContext.repoCrudSpec)

