package longevity.integration.oneShorthand

import longevity.IntegrationTest
import longevity.test.ScalaTestSpecs
import org.scalatest.Suites

@IntegrationTest
class MongoOneShorthandSpec extends Suites(longevityContext.repoPoolSpec)
