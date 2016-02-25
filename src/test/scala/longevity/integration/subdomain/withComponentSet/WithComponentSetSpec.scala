package longevity.integration.subdomain.withComponentSet

import org.scalatest.Suites

class WithComponentSetSpec extends Suites(
  context.mongoContext.inMemRepoCrudSpec,
  context.mongoContext.repoCrudSpec,
  context.cassandraContext.repoCrudSpec)

