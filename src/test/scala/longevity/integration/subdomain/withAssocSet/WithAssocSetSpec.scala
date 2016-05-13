package longevity.integration.subdomain.withAssocSet

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class WithAssocSetSpec extends Suites(
  mongoContext.inMemRepoCrudSpec,
  mongoContext.repoCrudSpec,
  cassandraContext.repoCrudSpec)

