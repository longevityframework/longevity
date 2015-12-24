package longevity.integration.subdomain.withAssocList

import org.scalatest.Suites

class InMemWithAssocListSpec extends Suites(context.mongoContext.inMemRepoCrudSpec)
