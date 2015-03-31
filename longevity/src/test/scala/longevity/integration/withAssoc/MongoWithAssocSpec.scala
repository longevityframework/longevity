package longevity.integration.withAssoc

import longevity.IntegrationTest

import org.scalatest.Suites

@IntegrationTest
class MongoWithAssocSpec extends Suites(longevityContext.repoPoolSpec)
