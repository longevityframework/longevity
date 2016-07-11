package longevity.integration.subdomain.shorthandLists

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class ShorthandListsSpec extends Suites(
  mongoContext.inMemRepoCrudSpec,
  mongoContext.repoCrudSpec,
  cassandraContext.repoCrudSpec)
