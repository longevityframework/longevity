package longevity.integration.subdomain.withComplexConstraint

import org.scalatest.Suites

class WithComplexConstraintSpec extends Suites(
  context.mongoContext.inMemRepoCrudSpec,
  context.mongoContext.repoCrudSpec,
  context.cassandraContext.repoCrudSpec)

