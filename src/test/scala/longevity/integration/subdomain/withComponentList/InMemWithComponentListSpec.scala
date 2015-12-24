package longevity.integration.subdomain.withComponentList

import org.scalatest.Suites

class InMemWithComponentListSpec extends Suites(context.mongoContext.inMemRepoCrudSpec)
