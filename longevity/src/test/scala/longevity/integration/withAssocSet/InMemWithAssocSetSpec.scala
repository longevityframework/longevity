package longevity.integration.withAssocSet

import longevity.testUtil.RepoPoolSpec
import longevity.IntegrationTest

@IntegrationTest
class InMemWithAssocSetSpec
extends RepoPoolSpec(boundedContext, inMemRepoPool, suiteNameSuffix = Some("(InMem)"))
