package longevity.integration.master

import longevity.IntegrationTest
import longevity.MasterIntegrationTest
import longevity.test.ScalaTestSpecs
import org.scalatest.Suites

@IntegrationTest
@MasterIntegrationTest
class InMemMasterSpec extends Suites(longevityContext.inMemRepoPoolSpec)
