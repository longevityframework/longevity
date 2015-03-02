package musette.repo.inmem

import longevity.repo._
import musette.domain.Wiki
import musette.domain.WikiType
import musette.repo.WikiRepo

class InMemWikiRepo(
  implicit repoPool: OldRepoPool
)
extends InMemRepo[Wiki](WikiType)
with WikiRepo
