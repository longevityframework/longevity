package musette.repo.mongo

import musette.domain.Comment
import musette.repo.MusetteRepoSpec

class MongoCommentRepoSpec extends MusetteRepoSpec[Comment] {

  private val repoLayer = new MongoRepoLayer
  def ename = "comment"
  def repo = repoLayer.commentRepo

}
