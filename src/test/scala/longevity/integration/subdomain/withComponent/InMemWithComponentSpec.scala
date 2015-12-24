package longevity.integration.subdomain.withComponent

import org.scalatest.Suites

class InMemWithComponentSpec extends Suites(context.mongoContext.inMemRepoCrudSpec)
