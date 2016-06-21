package longevity.integration.subdomain.withSinglePropComponent

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class WithSinglePropComponentSpec extends Suites(
  mongoContext.inMemRepoCrudSpec,
  mongoContext.repoCrudSpec,
  cassandraContext.repoCrudSpec)

