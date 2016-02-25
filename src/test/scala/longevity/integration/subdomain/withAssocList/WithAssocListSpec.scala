package longevity.integration.subdomain.withAssocList

import org.scalatest.Suites

class WithAssocListSpec extends Suites(
  context.mongoContext.inMemRepoCrudSpec,
  context.mongoContext.repoCrudSpec,
  context.cassandraContext.repoCrudSpec)

