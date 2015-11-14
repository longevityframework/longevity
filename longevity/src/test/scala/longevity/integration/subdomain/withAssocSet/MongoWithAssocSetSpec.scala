package longevity.integration.subdomain.withAssocSet

import org.scalatest.Suites

class MongoWithAssocSetSpec extends Suites(context.mongoContext.repoPoolSpec)
