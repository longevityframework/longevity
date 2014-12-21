package musette.repo.inmem

import longevity.repo._
import musette.domain.BlogPost
import musette.repo.BlogPostRepo

class InMemBlogPostRepo(
  implicit override protected val repoPool: RepoPool
)
extends InMemRepo[BlogPost](BlogPost)
with BlogPostRepo
