package longevity.integration.subdomain.componentSet

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class WithComponentSetSpec extends Suites(
  mongoContext.inMemRepoCrudSpec,
  mongoContext.repoCrudSpec,
  cassandraContext.repoCrudSpec)

