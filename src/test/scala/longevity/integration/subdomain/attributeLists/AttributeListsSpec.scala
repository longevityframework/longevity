package longevity.integration.subdomain.attributeLists

import org.scalatest.Suites

class AttributeListsSpec extends Suites(
  context.mongoContext.inMemRepoCrudSpec,
  context.mongoContext.repoCrudSpec,
  context.cassandraContext.repoCrudSpec)
