package longevity.integration.subdomain.withAssoc

import org.scalatest.Suites

class InMemWithAssocSpec extends Suites(context.mongoContext.inMemRepoPoolSpec)
