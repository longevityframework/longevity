package longevity.integration.allAttributes

import longevity.IntegrationTest

import org.scalatest.Suites

@IntegrationTest
class InMemAllAttributesSpec extends Suites(context.longevityContext.inMemRepoPoolSpec)
