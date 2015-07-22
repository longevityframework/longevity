package longevity.integration.withComponentOption

import longevity.IntegrationTest

import org.scalatest.Suites

@IntegrationTest
class InMemWithComponentOptionSpec extends Suites(longevityContext.inMemRepoPoolSpec)
