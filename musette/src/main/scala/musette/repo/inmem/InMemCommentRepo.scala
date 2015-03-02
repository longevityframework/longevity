package musette.repo.inmem

import longevity.repo._
import musette.domain.Comment
import musette.domain.CommentType
import musette.repo.CommentRepo

class InMemCommentRepo(
  implicit repoPool: OldRepoPool
)
extends InMemRepo[Comment](CommentType)
with CommentRepo
