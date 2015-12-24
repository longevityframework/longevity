package longevity.integration.subdomain.oneAttribute

import org.scalatest.Suites

class InMemOneAttributeSpec extends Suites(context.mongoContext.inMemRepoCrudSpec)
