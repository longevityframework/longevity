package longevity.integration.oneAttribute

import longevity.testUtil.RepoPoolSpec
import longevity.IntegrationTest

@IntegrationTest
class MongoOneAttributeSpec
extends RepoPoolSpec(boundedContext, mongoRepoPool, suiteNameSuffix = Some("(Mongo)"))
