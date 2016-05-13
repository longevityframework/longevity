package longevity.integration.subdomain.withComponentList

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class WithComponentListSpec extends Suites(
  mongoContext.inMemRepoCrudSpec,
  mongoContext.repoCrudSpec,
  cassandraContext.repoCrudSpec)
