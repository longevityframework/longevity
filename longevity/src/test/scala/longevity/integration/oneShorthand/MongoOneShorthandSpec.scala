package longevity.integration.oneShorthand

import longevity.testUtil.RepoPoolSpec
import longevity.IntegrationTest

@IntegrationTest
class MongoOneShorthandSpec
extends RepoPoolSpec(boundedContext, mongoRepoPool, suiteNameSuffix = Some("(Mongo)"))
