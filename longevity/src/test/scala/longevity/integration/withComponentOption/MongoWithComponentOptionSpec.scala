package longevity.integration.withComponentOption

import longevity.IntegrationTest

import org.scalatest.Suites

@IntegrationTest
class MongoWithComponentOptionSpec extends Suites(longevityContext.repoPoolSpec)
