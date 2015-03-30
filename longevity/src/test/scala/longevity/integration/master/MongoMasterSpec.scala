package longevity.integration.master

import longevity.IntegrationTest
import longevity.MasterIntegrationTest
import longevity.test.ScalaTestSpecs
import org.scalatest.Suites

@IntegrationTest
@MasterIntegrationTest
class MongoMasterSpec extends Suites(longevityContext.repoPoolSpec)
