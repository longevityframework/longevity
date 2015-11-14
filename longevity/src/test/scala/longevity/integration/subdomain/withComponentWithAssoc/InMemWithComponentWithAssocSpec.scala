package longevity.integration.subdomain.withComponentWithAssoc

import org.scalatest.Suites

class InMemWithComponentWithAssocSpec extends Suites(context.mongoContext.inMemRepoPoolSpec)
