package longevity.integration.subdomain.componentWithForeignKey

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class WithComponentWithForeignKeySpec extends Suites(
  mongoContext.inMemRepoCrudSpec,
  mongoContext.repoCrudSpec,
  cassandraContext.repoCrudSpec)

