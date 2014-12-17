package musette.repo.inmem

import longevity.repo._
import musette.domain.Wiki
import musette.repo.WikiRepo

class InMemWikiRepo(
  implicit override protected val repoPool: RepoPool
)
extends WikiRepo with InMemRepo[Wiki] {
  override val entityType = Wiki
}
