package longevity.integration.subdomain.attributeLists

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class AttributeListsSpec extends Suites(
  context.mongoContext.inMemRepoCrudSpec,
  context.mongoContext.repoCrudSpec,
  context.cassandraContext.repoCrudSpec)
