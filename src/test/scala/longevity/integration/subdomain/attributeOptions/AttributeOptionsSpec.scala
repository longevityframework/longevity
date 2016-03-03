package longevity.integration.subdomain.attributeOptions

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class AttributeOptionsSpec extends Suites(
  context.mongoContext.inMemRepoCrudSpec,
  context.mongoContext.repoCrudSpec,
  context.cassandraContext.repoCrudSpec)
