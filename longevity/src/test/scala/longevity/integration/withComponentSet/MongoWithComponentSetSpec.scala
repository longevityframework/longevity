package longevity.integration.withComponentSet

import longevity.IntegrationTest

import org.scalatest.Suites

@IntegrationTest
class MongoWithComponentSetSpec extends Suites(longevityContext.repoPoolSpec)
