package longevity.integration.subdomain.attributeLists

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class AttributeListsSpec extends Suites(
  mongoContext.inMemRepoCrudSpec,
  mongoContext.repoCrudSpec,
  cassandraContext.repoCrudSpec)
