package musette.repo
package inmem

import longevity.repo._
import musette.domain.Blog

class InMemBlogRepo(
  implicit override protected val repoPool: RepoPool
)
extends InMemRepo[Blog](Blog)
with BlogRepo
