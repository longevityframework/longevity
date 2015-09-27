package longevity.integration.oneShorthand

import longevity.IntegrationTest

import org.scalatest.Suites

@IntegrationTest
class InMemOneShorthandSpec extends Suites(context.longevityContext.inMemRepoPoolSpec)
