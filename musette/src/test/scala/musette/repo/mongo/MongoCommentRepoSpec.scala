package musette.repo.mongo

import org.scalatest._
import org.scalatest.OptionValues._
import longevity.testUtils.RepoSpec
import musette.domain.testUtils._
import musette.domain.Comment

class MongoCommentRepoSpec extends RepoSpec[Comment] {

  private val repoLayer = new MongoRepoLayer
  def ename = "comment"
  def repo = repoLayer.commentRepo
  def genTestEntity = testEntityGen.comment _
  def updateTestEntity = { e => e.copy(uri = e.uri + "77") }
  def persistedShouldMatchUnpersisted = entityMatchers.persistedCommentShouldMatchUnpersisted _

}
