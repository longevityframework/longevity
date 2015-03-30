package longevity.integration.oneAttribute

import longevity.IntegrationTest
import longevity.test.ScalaTestSpecs
import org.scalatest.Suites

@IntegrationTest
class InMemOneAttributeSpec extends Suites(longevityContext.inMemRepoPoolSpec)
