package longevity.integration.subdomain.componentShorthands

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class WithComponentShorthandsSpec extends Suites(
  mongoContext.inMemRepoCrudSpec,
  mongoContext.repoCrudSpec,
  cassandraContext.repoCrudSpec)


