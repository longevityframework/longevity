package longevity.integration.subdomain.withAssocSet

import org.scalatest.Suites

class WithAssocSetSpec extends Suites(
  context.mongoContext.inMemRepoCrudSpec,
  context.mongoContext.repoCrudSpec,
  context.cassandraContext.repoCrudSpec)

