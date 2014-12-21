package musette.repo.inmem

import longevity.repo._
import musette.domain.Blog
import musette.repo.BlogRepo

class InMemBlogRepo(
  implicit override protected val repoPool: RepoPool
)
extends InMemRepo[Blog](Blog)
with BlogRepo
