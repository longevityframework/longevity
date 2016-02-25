package longevity.integration.subdomain.oneShorthand

import org.scalatest.Suites

class OneShorthandSpec extends Suites(
  context.mongoContext.inMemRepoCrudSpec,
  context.mongoContext.repoCrudSpec,
  context.cassandraContext.repoCrudSpec)

