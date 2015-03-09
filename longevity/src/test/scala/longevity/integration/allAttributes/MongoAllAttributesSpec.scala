package longevity.integration.allAttributes

import longevity.testUtil.RepoPoolSpec
import longevity.IntegrationTest

@IntegrationTest
class MongoAllAttributesSpec
extends RepoPoolSpec(boundedContext, mongoRepoPool, suiteNameSuffix = Some("(Mongo)"))
