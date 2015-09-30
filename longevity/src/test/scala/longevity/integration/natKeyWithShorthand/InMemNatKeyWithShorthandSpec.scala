package longevity.integration.natKeyWithShorthand

import longevity.IntegrationTest

import org.scalatest.Suites

@IntegrationTest
class InMemNatKeyWithShorthandSpec extends Suites(context.longevityContext.inMemRepoPoolSpec)
