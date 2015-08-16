package longevity.integration.withComponentWithAssoc

import longevity.IntegrationTest

import org.scalatest.Suites

@IntegrationTest
class InMemWithComponentWithAssocSpec extends Suites(longevityContext.inMemRepoPoolSpec)
