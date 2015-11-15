package longevity.integration.subdomain.multipleKeys

import org.scalatest.Suites

class MongoMultipleKeysSpec extends Suites(context.mongoContext.repoPoolSpec)
