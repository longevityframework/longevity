package longevity.integration.subdomain.withSimpleConstraint

import org.scalatest.Suites

class WithSimpleConstraintSpec extends Suites(
  context.mongoContext.inMemRepoCrudSpec,
  context.mongoContext.repoCrudSpec,
  context.cassandraContext.repoCrudSpec)

