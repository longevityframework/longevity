package longevity.integration.subdomain.oneAttribute

import org.scalatest.Suites

class OneAttributeSpec extends Suites(
  context.mongoContext.inMemRepoCrudSpec,
  context.mongoContext.repoCrudSpec,
  context.cassandraContext.repoCrudSpec)
