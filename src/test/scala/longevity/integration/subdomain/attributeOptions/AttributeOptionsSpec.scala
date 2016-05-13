package longevity.integration.subdomain.attributeOptions

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class AttributeOptionsSpec extends Suites(
  mongoContext.inMemRepoCrudSpec,
  mongoContext.repoCrudSpec,
  cassandraContext.repoCrudSpec)
