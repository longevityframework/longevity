package longevity.integration.subdomain.allShorthands

import org.scalatest.Suites

class MongoAllShorthandsSpec extends Suites(context.mongoContext.repoCrudSpec)
