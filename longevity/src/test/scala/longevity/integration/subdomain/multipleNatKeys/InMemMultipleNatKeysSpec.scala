package longevity.integration.subdomain.multipleNatKeys

import org.scalatest.Suites

class InMemMultipleNatKeysSpec extends Suites(context.mongoContext.inMemRepoPoolSpec)
