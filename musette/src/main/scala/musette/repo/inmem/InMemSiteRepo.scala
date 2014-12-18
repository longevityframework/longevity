package musette.repo.inmem

import longevity.repo._
import musette.domain.Site
import musette.repo.SiteRepo

class InMemSiteRepo(
  implicit override protected val repoPool: RepoPool
)
extends SiteRepo with InMemRepo[Site] {
  override val entityType = Site
}
