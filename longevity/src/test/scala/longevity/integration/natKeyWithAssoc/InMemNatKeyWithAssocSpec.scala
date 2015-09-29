package longevity.integration.natKeyWithAssoc

import longevity.IntegrationTest

import org.scalatest.Suites

@IntegrationTest
class InMemNatKeyWithAssocSpec extends Suites(context.longevityContext.inMemRepoPoolSpec)
