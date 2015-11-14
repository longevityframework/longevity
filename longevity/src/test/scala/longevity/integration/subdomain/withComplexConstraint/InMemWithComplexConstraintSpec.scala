package longevity.integration.subdomain.withComplexConstraint

import org.scalatest.Suites

class InMemWithComplexConstraintSpec extends Suites(context.mongoContext.inMemRepoPoolSpec)
