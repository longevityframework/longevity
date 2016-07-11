package longevity.integration.subdomain.foreignKey

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class WithForeignKeySpec extends Suites(
  mongoContext.inMemRepoCrudSpec,
  mongoContext.repoCrudSpec,
  cassandraContext.repoCrudSpec)
