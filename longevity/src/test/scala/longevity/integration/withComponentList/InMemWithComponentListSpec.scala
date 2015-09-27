package longevity.integration.withComponentList

import longevity.IntegrationTest

import org.scalatest.Suites

@IntegrationTest
class InMemWithComponentListSpec extends Suites(context.longevityContext.inMemRepoPoolSpec)
