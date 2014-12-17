package musette.repo.inmem

import longevity.repo._
import musette.domain.Comment
import musette.repo.CommentRepo

class InMemCommentRepo(
  implicit override protected val repoPool: RepoPool
)
extends CommentRepo with InMemRepo[Comment] {
  override val entityType = Comment
}
