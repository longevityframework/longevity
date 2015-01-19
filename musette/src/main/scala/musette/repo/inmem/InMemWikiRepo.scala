package musette.repo.inmem

import longevity.repo._
import musette.domain.Wiki
import musette.domain.WikiType
import musette.repo.WikiRepo

class InMemWikiRepo(
  implicit override protected val repoPool: RepoPool
)
extends InMemRepo[Wiki](WikiType)
with WikiRepo
