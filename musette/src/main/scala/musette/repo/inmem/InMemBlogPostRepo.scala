package musette.repo
package inmem

import longevity.repo._
import musette.domain.BlogPost

class InMemBlogPostRepo(
  implicit override protected val repoPool: RepoPool
)
extends InMemRepo[BlogPost](BlogPost)
with BlogPostRepo
