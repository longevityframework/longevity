package longevity.integration.allAttributes

import longevity.IntegrationTest

import org.scalatest.Suites

@IntegrationTest
class MongoAllAttributesSpec extends Suites(longevityContext.repoPoolSpec)
