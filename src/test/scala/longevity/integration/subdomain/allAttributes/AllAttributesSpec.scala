package longevity.integration.subdomain.allAttributes

import org.scalatest.Suites

class AllAttributesSpec extends Suites(
  context.mongoContext.inMemRepoCrudSpec,
  // context.mongoContext.repoCrudSpec)
  context.mongoContext.repoCrudSpec,
  context.cassandraContext.repoCrudSpec)
