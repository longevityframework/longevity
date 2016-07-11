package longevity.integration.subdomain.componentWithSet

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class WithComponentWithSetSpec extends Suites(
  mongoContext.inMemRepoCrudSpec,
  mongoContext.repoCrudSpec,
  cassandraContext.repoCrudSpec)

