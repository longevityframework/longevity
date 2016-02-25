package longevity.integration.subdomain.withAssoc

import org.scalatest.Suites

class WithAssocSpec extends Suites(
  context.mongoContext.inMemRepoCrudSpec,
  context.mongoContext.repoCrudSpec,
  context.cassandraContext.repoCrudSpec)
