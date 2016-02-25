package longevity.integration.subdomain.withComponent

import org.scalatest.Suites

class WithComponentSpec extends Suites(
  context.mongoContext.inMemRepoCrudSpec,
  context.mongoContext.repoCrudSpec,
  context.cassandraContext.repoCrudSpec)

