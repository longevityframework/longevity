package longevity.integration.subdomain.allAttributes

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class AllAttributesSpec extends Suites(
  mongoContext.inMemRepoCrudSpec,
  mongoContext.repoCrudSpec,
  cassandraContext.repoCrudSpec)
