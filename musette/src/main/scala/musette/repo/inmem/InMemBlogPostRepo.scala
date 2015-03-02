package musette.repo.inmem

import longevity.repo._
import musette.domain.BlogPost
import musette.domain.BlogPostType
import musette.repo.BlogPostRepo

class InMemBlogPostRepo(
  implicit override protected val repoPool: OldRepoPool
)
extends InMemRepo[BlogPost](BlogPostType)
with BlogPostRepo
