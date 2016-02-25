package longevity.integration.subdomain.withComponentWithShorthands

import org.scalatest.Suites

class WithComponentWithShorthandsSpec extends Suites(
  context.mongoContext.inMemRepoCrudSpec,
  context.mongoContext.repoCrudSpec,
  context.cassandraContext.repoCrudSpec)


