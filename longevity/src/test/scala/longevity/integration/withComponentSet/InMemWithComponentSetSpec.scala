package longevity.integration.withComponentSet

import longevity.IntegrationTest

import org.scalatest.Suites

@IntegrationTest
class InMemWithComponentSetSpec extends Suites(longevityContext.inMemRepoPoolSpec)
