package longevity.integration.withAssocSet

import longevity.testUtil.RepoPoolSpec
import longevity.IntegrationTest

@IntegrationTest
class MongoWithAssocSetSpec
extends RepoPoolSpec(boundedContext, mongoRepoPool, suiteNameSuffix = Some("(Mongo)"))
