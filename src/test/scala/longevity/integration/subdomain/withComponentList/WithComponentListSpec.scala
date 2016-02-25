package longevity.integration.subdomain.withComponentList

import org.scalatest.Suites

class WithComponentListSpec extends Suites(
  context.mongoContext.inMemRepoCrudSpec,
  context.mongoContext.repoCrudSpec,
  context.cassandraContext.repoCrudSpec)
