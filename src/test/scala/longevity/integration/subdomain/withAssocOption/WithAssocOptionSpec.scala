package longevity.integration.subdomain.withAssocOption

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class WithAssocOptionSpec extends Suites(
  mongoContext.inMemRepoCrudSpec,
  mongoContext.repoCrudSpec,
  cassandraContext.repoCrudSpec)
