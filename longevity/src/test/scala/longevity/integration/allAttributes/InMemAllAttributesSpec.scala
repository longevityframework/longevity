package longevity.integration.allAttributes

import longevity.testUtil.RepoPoolSpec
import longevity.IntegrationTest

@IntegrationTest
class InMemAllAttributesSpec
extends RepoPoolSpec(boundedContext, inMemRepoPool, suiteNameSuffix = Some("(InMem)"))
