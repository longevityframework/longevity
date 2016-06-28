package longevity.integration.subdomain.withComponentWithForeignKey

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class WithComponentWithForeignKeySpec extends Suites(
  mongoContext.inMemRepoCrudSpec,
  mongoContext.repoCrudSpec,
  cassandraContext.repoCrudSpec)

