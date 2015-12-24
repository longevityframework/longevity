package longevity.integration.subdomain.withSimpleConstraint

import org.scalatest.Suites

class InMemWithSimpleConstraintSpec extends Suites(context.mongoContext.inMemRepoCrudSpec)
