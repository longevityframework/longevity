package musette.repo.inmem

import musette.domain.testUtil.entityMatchers
import musette.domain.Comment
import musette.repo.MusetteRepoSpec

class InMemCommentRepoSpec extends MusetteRepoSpec[Comment] {

  private val repoLayer = new InMemRepoLayer
  def ename = "comment"
  def repo = repoLayer.commentRepo
  def persistedShouldMatchUnpersisted = entityMatchers.persistedCommentShouldMatchUnpersisted _

}
