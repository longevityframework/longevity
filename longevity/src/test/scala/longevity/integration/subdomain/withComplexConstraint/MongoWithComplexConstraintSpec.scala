package longevity.integration.subdomain.withComplexConstraint

import org.scalatest.Suites

class MongoWithComplexConstraintSpec extends Suites(context.mongoContext.repoPoolSpec)
