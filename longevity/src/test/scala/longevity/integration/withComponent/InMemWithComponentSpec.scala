package longevity.integration.withComponent

import longevity.IntegrationTest

import org.scalatest.Suites

@IntegrationTest
class InMemWithComponentSpec extends Suites(context.longevityContext.inMemRepoPoolSpec)
