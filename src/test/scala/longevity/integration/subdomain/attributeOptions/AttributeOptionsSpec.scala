package longevity.integration.subdomain.attributeOptions

import org.scalatest.Suites

class AttributeOptionsSpec extends Suites(
  context.mongoContext.inMemRepoCrudSpec,
  context.mongoContext.repoCrudSpec,
  context.cassandraContext.repoCrudSpec)
