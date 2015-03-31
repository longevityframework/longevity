package longevity.integration.master

import longevity.IntegrationTest
import longevity.MasterIntegrationTest

import org.scalatest.Suites

@IntegrationTest
@MasterIntegrationTest
class InMemMasterSpec extends Suites(longevityContext.inMemRepoPoolSpec)
