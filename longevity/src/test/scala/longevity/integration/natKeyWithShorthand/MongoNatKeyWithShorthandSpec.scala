package longevity.integration.natKeyWithShorthand

import longevity.IntegrationTest

import org.scalatest.Suites

@IntegrationTest
class MongoNatKeyWithShorthandSpec extends Suites(context.longevityContext.repoPoolSpec)
