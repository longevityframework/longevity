package longevity.integration.subdomain.keyWithAssoc

import org.scalatest.Suites

class InMemKeyWithAssocSpec extends Suites(context.mongoContext.inMemRepoPoolSpec)
