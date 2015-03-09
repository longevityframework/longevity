package longevity.integration.master

import longevity.testUtil.RepoPoolSpec
import longevity.IntegrationTest
import longevity.MasterIntegrationTest

@IntegrationTest
@MasterIntegrationTest
class MongoMasterSpec extends RepoPoolSpec(boundedContext, mongoRepoPool, suiteNameSuffix = Some("(Mongo)"))
