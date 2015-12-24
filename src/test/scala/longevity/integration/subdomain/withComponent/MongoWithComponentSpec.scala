package longevity.integration.subdomain.withComponent

import org.scalatest.Suites

class MongoWithComponentSpec extends Suites(context.mongoContext.repoCrudSpec)
