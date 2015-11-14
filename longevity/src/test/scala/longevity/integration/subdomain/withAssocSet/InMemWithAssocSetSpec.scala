package longevity.integration.subdomain.withAssocSet

import org.scalatest.Suites

class InMemWithAssocSetSpec extends Suites(context.mongoContext.inMemRepoPoolSpec)
