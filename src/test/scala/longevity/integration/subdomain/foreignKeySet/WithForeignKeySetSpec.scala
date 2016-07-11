package longevity.integration.subdomain.foreignKeySet

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class WithForeignKeySetSpec extends Suites(
  mongoContext.inMemRepoCrudSpec,
  mongoContext.repoCrudSpec,
  cassandraContext.repoCrudSpec)

