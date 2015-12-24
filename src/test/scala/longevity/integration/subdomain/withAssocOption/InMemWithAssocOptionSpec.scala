package longevity.integration.subdomain.withAssocOption

import org.scalatest.Suites

class InMemWithAssocOptionSpec extends Suites(context.mongoContext.inMemRepoCrudSpec)
