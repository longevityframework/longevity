package musette.repo.inmem

import longevity.repo._
import musette.domain.WikiPage
import musette.domain.WikiPageType
import musette.repo.WikiPageRepo

class InMemWikiPageRepo(
  implicit override protected val repoPool: RepoPool
)
extends InMemRepo[WikiPage](WikiPageType)
with WikiPageRepo
