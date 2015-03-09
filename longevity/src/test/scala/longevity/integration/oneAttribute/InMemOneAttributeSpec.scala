package longevity.integration.oneAttribute

import longevity.testUtil.RepoPoolSpec
import longevity.IntegrationTest

@IntegrationTest
class InMemOneAttributeSpec 
extends RepoPoolSpec(boundedContext, inMemRepoPool, suiteNameSuffix = Some("(InMem)"))
