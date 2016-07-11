package longevity.integration.subdomain.basicSets

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class BasicSetsSpec extends Suites(
  mongoContext.inMemRepoCrudSpec,
  mongoContext.repoCrudSpec,
  cassandraContext.repoCrudSpec)
