package longevity.integration.master

import longevity.IntegrationTest
import longevity.MasterIntegrationTest

import org.scalatest.Suites

// TODO: build commands such as master:test are broken (no scenarios run) please fix

@IntegrationTest
@MasterIntegrationTest
class MongoMasterSpec extends Suites(context.longevityContext.repoPoolSpec)
