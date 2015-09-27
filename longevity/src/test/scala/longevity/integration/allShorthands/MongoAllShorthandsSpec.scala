package longevity.integration.allShorthands

import longevity.IntegrationTest

import org.scalatest.Suites

@IntegrationTest
class MongoAllShorthandsSpec extends Suites(context.longevityContext.repoPoolSpec)
