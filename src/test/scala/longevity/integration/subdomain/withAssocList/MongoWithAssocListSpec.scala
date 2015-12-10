package longevity.integration.subdomain.withAssocList

import org.scalatest.Suites

class MongoWithAssocListSpec extends Suites(context.mongoContext.repoPoolSpec)
