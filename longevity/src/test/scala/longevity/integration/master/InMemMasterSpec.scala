package longevity.integration.master

import longevity.testUtil.RepoPoolSpec
import longevity.IntegrationTest
import longevity.MasterIntegrationTest

@IntegrationTest
@MasterIntegrationTest
class InMemMasterSpec extends RepoPoolSpec(boundedContext, inMemRepoPool, suiteNameSuffix = Some("(InMem)"))
