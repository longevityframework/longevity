package longevity.integration.oneAttribute

import longevity.IntegrationTest

import org.scalatest.Suites

@IntegrationTest
class InMemOneAttributeSpec extends Suites(context.longevityContext.inMemRepoPoolSpec)
