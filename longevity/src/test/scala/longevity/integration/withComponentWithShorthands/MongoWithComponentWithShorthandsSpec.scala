package longevity.integration.withComponentWithShorthands

import longevity.IntegrationTest

import org.scalatest.Suites

@IntegrationTest
class MongoWithComponentWithShorthandsSpec extends Suites(context.longevityContext.repoPoolSpec)
