package longevity.integration.subdomain.multipleKeys

import org.scalatest.Suites

class InMemMultipleKeysSpec extends Suites(context.mongoContext.inMemRepoPoolSpec)
