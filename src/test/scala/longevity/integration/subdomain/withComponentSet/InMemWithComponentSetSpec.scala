package longevity.integration.subdomain.withComponentSet

import org.scalatest.Suites

class InMemWithComponentSetSpec extends Suites(context.mongoContext.inMemRepoCrudSpec)
