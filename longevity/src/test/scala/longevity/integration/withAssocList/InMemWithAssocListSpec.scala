package longevity.integration.withAssocList

import longevity.IntegrationTest

import org.scalatest.Suites

@IntegrationTest
class InMemWithAssocListSpec extends Suites(context.longevityContext.inMemRepoPoolSpec)
