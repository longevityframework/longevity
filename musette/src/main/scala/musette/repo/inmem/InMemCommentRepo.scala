package musette.repo.inmem

import longevity.repo._
import musette.domain.Comment
import musette.repo.CommentRepo

class InMemCommentRepo(
  implicit override protected val repoPool: RepoPool
)
extends InMemRepo[Comment](Comment)
with CommentRepo
