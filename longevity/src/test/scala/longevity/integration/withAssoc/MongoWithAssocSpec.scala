package longevity.integration.withAssoc

import longevity.testUtil.RepoPoolSpec
import longevity.IntegrationTest

@IntegrationTest
class MongoWithAssocSpec
extends RepoPoolSpec(boundedContext, mongoRepoPool, suiteNameSuffix = Some("(Mongo)"))
