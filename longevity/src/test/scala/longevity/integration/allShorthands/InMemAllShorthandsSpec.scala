package longevity.integration.allShorthands

import longevity.IntegrationTest

import org.scalatest.Suites

@IntegrationTest
class InMemAllShorthandsSpec extends Suites(context.longevityContext.inMemRepoPoolSpec)
