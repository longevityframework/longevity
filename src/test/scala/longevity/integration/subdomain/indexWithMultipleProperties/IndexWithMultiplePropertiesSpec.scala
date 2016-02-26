package longevity.integration.subdomain.indexWithMultipleProperties

import org.scalatest.Suites

class IndexWithMultiplePropertiesSpec extends Suites(
  context.mongoContext.inMemRepoCrudSpec,
  context.mongoContext.repoCrudSpec,
  context.cassandraContext.repoCrudSpec)
