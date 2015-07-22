package longevity.integration.withComponent

import longevity.IntegrationTest

import org.scalatest.Suites

@IntegrationTest
class InMemWithAssocSpec extends Suites(longevityContext.inMemRepoPoolSpec)
