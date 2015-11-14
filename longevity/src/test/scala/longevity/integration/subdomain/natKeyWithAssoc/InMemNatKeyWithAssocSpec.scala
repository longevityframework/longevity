package longevity.integration.subdomain.natKeyWithAssoc

import org.scalatest.Suites

class InMemNatKeyWithAssocSpec extends Suites(context.mongoContext.inMemRepoPoolSpec)
