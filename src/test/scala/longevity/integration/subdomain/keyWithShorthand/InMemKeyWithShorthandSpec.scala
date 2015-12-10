package longevity.integration.subdomain.keyWithShorthand

import org.scalatest.Suites

class InMemKeyWithShorthandSpec extends Suites(context.mongoContext.inMemRepoPoolSpec)
