package longevity.integration.subdomain.withComponentSet

import org.scalatest.Suites

class MongoWithComponentSetSpec extends Suites(context.mongoContext.repoCrudSpec)
