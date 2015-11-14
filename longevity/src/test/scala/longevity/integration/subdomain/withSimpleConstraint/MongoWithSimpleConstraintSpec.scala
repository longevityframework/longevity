package longevity.integration.subdomain.withSimpleConstraint

import org.scalatest.Suites

class MongoWithSimpleConstraintSpec extends Suites(context.mongoContext.repoPoolSpec)
