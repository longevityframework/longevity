package musette.repo.inmem

import longevity.repo._
import musette.domain.Site
import musette.domain.SiteType
import musette.repo.SiteRepo

class InMemSiteRepo(
  implicit repoPool: OldRepoPool
)
extends InMemRepo[Site](SiteType)
with SiteRepo
