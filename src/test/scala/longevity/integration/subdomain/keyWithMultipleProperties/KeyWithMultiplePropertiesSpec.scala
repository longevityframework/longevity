package longevity.integration.subdomain.keyWithMultipleProperties

import org.scalatest.Suites

class KeyWithMultiplePropertiesSpec extends Suites(
  context.mongoContext.inMemRepoCrudSpec,
  context.mongoContext.repoCrudSpec,
  context.cassandraContext.repoCrudSpec)
