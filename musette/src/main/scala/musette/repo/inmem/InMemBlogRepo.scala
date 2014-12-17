package musette.repo.inmem

import longevity.repo._
import musette.domain.Blog
import musette.repo.BlogRepo

class InMemBlogRepo(
  implicit override protected val repoPool: RepoPool
)
extends BlogRepo with InMemRepo[Blog] {
  override val entityType = Blog
}
