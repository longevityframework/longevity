package longevity.integration.subdomain.keyWithAssoc

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class KeyWithAssocSpec extends Suites(
  mongoContext.inMemRepoCrudSpec,
  mongoContext.repoCrudSpec,
  cassandraContext.repoCrudSpec)
