package musette.repo.inmem

import longevity.repo._
import musette.domain.Blog
import musette.domain.BlogType
import musette.repo.BlogRepo

class InMemBlogRepo(implicit repoPool: OldRepoPool)
extends InMemRepo[Blog](BlogType)
with BlogRepo
