package longevity.integration.oneShorthand

import longevity.testUtil.RepoPoolSpec
import longevity.IntegrationTest

@IntegrationTest
class InMemOneShorthandSpec
extends RepoPoolSpec(boundedContext, inMemRepoPool, suiteNameSuffix = Some("(InMem)"))
