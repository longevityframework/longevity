package longevity.integration.subdomain.shorthands

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class ShorthandsSpec extends Suites(
  mongoContext.inMemRepoCrudSpec,
  mongoContext.repoCrudSpec,
  cassandraContext.repoCrudSpec)
