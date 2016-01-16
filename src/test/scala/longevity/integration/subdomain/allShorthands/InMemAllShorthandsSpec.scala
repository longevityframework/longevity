package longevity.integration.subdomain.allShorthands


import org.scalatest.Suites

class InMemAllShorthandsSpec extends Suites(context.mongoContext.inMemRepoCrudSpec)
