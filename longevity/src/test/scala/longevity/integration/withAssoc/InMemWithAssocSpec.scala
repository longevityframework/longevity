package longevity.integration.withAssoc

import longevity.testUtil.RepoPoolSpec
import longevity.IntegrationTest

@IntegrationTest
class InMemWithAssocSpec
extends RepoPoolSpec(boundedContext, inMemRepoPool, suiteNameSuffix = Some("(InMem)"))
