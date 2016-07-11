package longevity.integration.subdomain.keyWithForeignKey

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class KeyWithForeignKeySpec extends Suites(
  mongoContext.inMemRepoCrudSpec,
  mongoContext.repoCrudSpec,
  cassandraContext.repoCrudSpec)
