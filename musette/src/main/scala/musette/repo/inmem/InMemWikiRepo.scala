package musette.repo
package inmem

import longevity.repo._
import musette.domain.Wiki

class InMemWikiRepo(
  implicit override protected val repoPool: RepoPool
)
extends InMemRepo[Wiki](Wiki)
with WikiRepo
