package longevity.integration.oneShorthand

import longevity.IntegrationTest

import org.scalatest.Suites

@IntegrationTest
class MongoOneShorthandSpec extends Suites(context.longevityContext.repoPoolSpec)
