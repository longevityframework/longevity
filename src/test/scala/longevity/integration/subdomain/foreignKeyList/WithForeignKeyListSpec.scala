package longevity.integration.subdomain.foreignKeyList

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class WithForeignKeyListSpec extends Suites(
  mongoContext.inMemRepoCrudSpec,
  mongoContext.repoCrudSpec,
  cassandraContext.repoCrudSpec)

