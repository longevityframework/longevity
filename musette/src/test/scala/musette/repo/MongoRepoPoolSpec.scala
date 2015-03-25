package musette.repo

import longevity.testUtil.RepoPoolSpec
import musette.domain.boundedContext

class MongoRepoPoolSpec extends RepoPoolSpec(
  boundedContext,
  boundedContext.repoPool,
  suiteNameSuffix = Some("(Mongo)"))
