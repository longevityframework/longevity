package longevity.integration.allAttributes

import longevity.IntegrationTest
import longevity.test.ScalaTestSpecs
import org.scalatest.Suites

@IntegrationTest
class InMemAllAttributesSpec extends Suites(longevityContext.inMemRepoPoolSpec)
