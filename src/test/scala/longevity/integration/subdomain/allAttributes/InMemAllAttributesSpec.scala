package longevity.integration.subdomain.allAttributes


import org.scalatest.Suites

class InMemAllAttributesSpec extends Suites(context.mongoContext.inMemRepoCrudSpec)
