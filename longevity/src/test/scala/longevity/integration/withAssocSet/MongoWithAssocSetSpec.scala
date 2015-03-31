package longevity.integration.withAssocSet

import longevity.IntegrationTest

import org.scalatest.Suites

@IntegrationTest
class MongoWithAssocSetSpec extends Suites(longevityContext.repoPoolSpec)
