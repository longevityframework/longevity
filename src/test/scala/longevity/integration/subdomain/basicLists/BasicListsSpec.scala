package longevity.integration.subdomain.basicLists

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class BasicListsSpec extends Suites(
  mongoContext.inMemRepoCrudSpec,
  mongoContext.repoCrudSpec,
  cassandraContext.repoCrudSpec)
