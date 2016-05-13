package longevity.integration.subdomain.withAssoc

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class WithAssocSpec extends Suites(
  mongoContext.inMemRepoCrudSpec,
  mongoContext.repoCrudSpec,
  cassandraContext.repoCrudSpec)
