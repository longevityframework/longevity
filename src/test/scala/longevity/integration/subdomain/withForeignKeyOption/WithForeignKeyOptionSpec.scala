package longevity.integration.subdomain.withForeignKeyOption

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class WithForeignKeyOptionSpec extends Suites(
  mongoContext.inMemRepoCrudSpec,
  mongoContext.repoCrudSpec,
  cassandraContext.repoCrudSpec)
