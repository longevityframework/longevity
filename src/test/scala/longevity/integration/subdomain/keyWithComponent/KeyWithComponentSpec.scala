package longevity.integration.subdomain.keyWithComponent

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class KeyWithComponentSpec extends Suites(
  mongoContext.inMemRepoCrudSpec,
  mongoContext.repoCrudSpec,
  cassandraContext.repoCrudSpec)
