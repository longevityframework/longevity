package longevity.integration.withAssocSet

import longevity.IntegrationTest

import org.scalatest.Suites

@IntegrationTest
class InMemWithAssocSetSpec extends Suites(longevityContext.inMemRepoPoolSpec)
