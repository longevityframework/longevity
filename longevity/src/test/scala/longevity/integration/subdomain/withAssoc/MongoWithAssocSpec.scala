package longevity.integration.subdomain.withAssoc

import org.scalatest.Suites

class MongoWithAssocSpec extends Suites(context.mongoContext.repoPoolSpec)
