package musette.repo.inmem

import longevity.repo._
import musette.domain.WikiPage
import musette.repo.WikiPageRepo

class InMemWikiPageRepo(
  implicit override protected val repoPool: RepoPool
)
extends InMemRepo[WikiPage](WikiPage)
with WikiPageRepo
