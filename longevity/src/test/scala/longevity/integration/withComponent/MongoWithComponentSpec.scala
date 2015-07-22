package longevity.integration.withComponent

import longevity.IntegrationTest

import org.scalatest.Suites

@IntegrationTest
class MongoWithComponentSpec extends Suites(longevityContext.repoPoolSpec)
