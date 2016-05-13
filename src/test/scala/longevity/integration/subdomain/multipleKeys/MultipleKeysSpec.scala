package longevity.integration.subdomain.multipleKeys

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class MultipleKeysSpec extends Suites(
  mongoContext.inMemRepoCrudSpec,
  mongoContext.repoCrudSpec,
  cassandraContext.repoCrudSpec)

