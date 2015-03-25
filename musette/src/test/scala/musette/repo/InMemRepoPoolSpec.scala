package musette.repo

import longevity.testUtil.RepoPoolSpec
import musette.domain.boundedContext

class InMemRepoPoolSpec extends RepoPoolSpec(
  boundedContext,
  longevity.repo.inMemRepoPool(boundedContext.subdomain),
  suiteNameSuffix = Some("(InMem)"))
