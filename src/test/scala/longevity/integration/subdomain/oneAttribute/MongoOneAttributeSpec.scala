package longevity.integration.subdomain.oneAttribute

import org.scalatest.Suites

class MongoOneAttributeSpec extends Suites(context.mongoContext.repoCrudSpec)
