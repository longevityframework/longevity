package musette.repo
package inmem

import longevity.repo._
import musette.domain.Site

class InMemSiteRepo(
  implicit override protected val repoPool: RepoPool
)
extends InMemRepo[Site](Site)
with SiteRepo
