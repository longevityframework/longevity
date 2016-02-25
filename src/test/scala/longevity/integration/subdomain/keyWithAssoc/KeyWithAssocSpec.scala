package longevity.integration.subdomain.keyWithAssoc

import org.scalatest.Suites

class KeyWithAssocSpec extends Suites(
  context.mongoContext.inMemRepoCrudSpec,
  context.mongoContext.repoCrudSpec,
  context.cassandraContext.repoCrudSpec)
