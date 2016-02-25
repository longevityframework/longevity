package longevity.integration.subdomain.keyWithShorthand

import org.scalatest.Suites

class KeyWithShorthandSpec extends Suites(
  context.mongoContext.inMemRepoCrudSpec,
  context.mongoContext.repoCrudSpec,
  context.cassandraContext.repoCrudSpec)

