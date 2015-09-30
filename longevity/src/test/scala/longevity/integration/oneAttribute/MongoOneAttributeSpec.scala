package longevity.integration.oneAttribute

import longevity.IntegrationTest

import org.scalatest.Suites

@IntegrationTest
class MongoOneAttributeSpec extends Suites(context.longevityContext.repoPoolSpec)
