package longevity.integration.allShorthands

import longevity.IntegrationTest
import longevity.test.ScalaTestSpecs
import org.scalatest.Suites

@IntegrationTest
class InMemAllShorthandsSpec extends Suites(longevityContext.inMemRepoPoolSpec)
