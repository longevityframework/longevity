package musette.repo
package inmem

import longevity.repo._
import musette.domain.WikiPage

class InMemWikiPageRepo(
  implicit override protected val repoPool: RepoPool
)
extends InMemRepo[WikiPage](WikiPage)
with WikiPageRepo
