package longevity.integration.subdomain.withAssocOption

import org.scalatest.Suites

class WithAssocOptionSpec extends Suites(
  context.mongoContext.inMemRepoCrudSpec,
  context.mongoContext.repoCrudSpec,
  context.cassandraContext.repoCrudSpec)
