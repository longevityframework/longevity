package longevity.integration.subdomain.withComponentOption

import org.scalatest.Suites

class MongoWithComponentOptionSpec extends Suites(context.mongoContext.repoCrudSpec)
