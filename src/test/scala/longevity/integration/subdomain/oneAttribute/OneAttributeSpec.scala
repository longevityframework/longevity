package longevity.integration.subdomain.oneAttribute

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class OneAttributeSpec extends Suites(
  mongoContext.inMemRepoCrudSpec,
  mongoContext.repoCrudSpec,
  cassandraContext.repoCrudSpec)
