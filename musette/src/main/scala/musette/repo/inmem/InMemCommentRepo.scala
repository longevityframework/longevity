package musette.repo
package inmem

import longevity.repo._
import musette.domain.Comment

class InMemCommentRepo(
  implicit override protected val repoPool: RepoPool
)
extends InMemRepo[Comment](Comment)
with CommentRepo
