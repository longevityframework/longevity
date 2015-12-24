package longevity.integration.subdomain.withComponentList

import org.scalatest.Suites

class MongoWithComponentListSpec extends Suites(context.mongoContext.repoCrudSpec)
