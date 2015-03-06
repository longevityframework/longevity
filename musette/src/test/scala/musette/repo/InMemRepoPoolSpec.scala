package musette.repo

import longevity.testUtil.RepoPoolSpec
import musette.domain.boundedContext

class InMemRepoPoolSpec extends RepoPoolSpec(
  boundedContext,
  inMemRepoPool,
  suiteNameSuffix = Some("(InMem)"))
