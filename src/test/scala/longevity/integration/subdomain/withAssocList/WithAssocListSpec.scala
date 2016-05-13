package longevity.integration.subdomain.withAssocList

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class WithAssocListSpec extends Suites(
  mongoContext.inMemRepoCrudSpec,
  mongoContext.repoCrudSpec,
  cassandraContext.repoCrudSpec)

