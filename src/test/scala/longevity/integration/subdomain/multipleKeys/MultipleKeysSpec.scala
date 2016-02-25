package longevity.integration.subdomain.multipleKeys

import org.scalatest.Suites

class MultipleKeysSpec extends Suites(
  context.mongoContext.inMemRepoCrudSpec,
  context.mongoContext.repoCrudSpec,
  context.cassandraContext.repoCrudSpec)

