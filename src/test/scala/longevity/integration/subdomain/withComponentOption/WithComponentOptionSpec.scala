package longevity.integration.subdomain.withComponentOption

import org.scalatest.Suites

class WithComponentOptionSpec extends Suites(
  context.mongoContext.inMemRepoCrudSpec,
  context.mongoContext.repoCrudSpec,
  context.cassandraContext.repoCrudSpec)
