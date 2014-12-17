package musette.repo.inmem

import longevity.repo._
import musette.domain.WikiPage
import musette.repo.WikiPageRepo

class InMemWikiPageRepo(
  implicit override protected val repoPool: RepoPool
)
extends WikiPageRepo with InMemRepo[WikiPage] {
  override val entityType = WikiPage
}
