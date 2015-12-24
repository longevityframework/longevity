package longevity.integration.subdomain.oneShorthand

import org.scalatest.Suites

class InMemOneShorthandSpec extends Suites(context.mongoContext.inMemRepoCrudSpec)
