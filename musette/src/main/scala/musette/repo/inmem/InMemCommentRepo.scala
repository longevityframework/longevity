package musette.repo.inmem

import longevity.repo._
import musette.domain.Comment
import musette.domain.CommentType
import musette.repo.CommentRepo

class InMemCommentRepo(
  implicit override protected val repoPool: RepoPool
)
extends InMemRepo[Comment](CommentType)
with CommentRepo
