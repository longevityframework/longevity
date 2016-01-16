package longevity.integration.subdomain.allAttributes

import org.scalatest.Suites

class MongoAllAttributesSpec extends Suites(context.mongoContext.repoCrudSpec)
