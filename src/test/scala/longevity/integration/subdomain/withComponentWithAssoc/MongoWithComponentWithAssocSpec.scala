package longevity.integration.subdomain.withComponentWithAssoc

import org.scalatest.Suites

class MongoWithComponentWithAssocSpec extends Suites(context.mongoContext.repoPoolSpec)
