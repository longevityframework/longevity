package longevity.integration.subdomain.withComponentWithAssoc

import org.scalatest.Suites

class WithComponentWithAssocSpec extends Suites(
  context.mongoContext.inMemRepoCrudSpec,
  context.mongoContext.repoCrudSpec,
  context.cassandraContext.repoCrudSpec)

