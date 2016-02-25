package longevity.integration.subdomain.attributeSets

import org.scalatest.Suites

class AttributeSetsSpec extends Suites(
  context.mongoContext.inMemRepoCrudSpec,
  context.mongoContext.repoCrudSpec,
  context.cassandraContext.repoCrudSpec)
