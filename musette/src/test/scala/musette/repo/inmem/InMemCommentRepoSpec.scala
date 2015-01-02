package musette.repo.inmem

import org.scalatest._
import org.scalatest.OptionValues._
import longevity.testUtil.RepoSpec
import musette.domain.testUtil._
import musette.domain.Comment

class InMemCommentRepoSpec extends RepoSpec[Comment] {

  private val repoLayer = new InMemRepoLayer
  def ename = "comment"
  def repo = repoLayer.commentRepo
  def genTestEntity = testEntityGen.comment _
  def updateTestEntity = { e => e.copy(uri = e.uri + "77") }
  def persistedShouldMatchUnpersisted = entityMatchers.persistedCommentShouldMatchUnpersisted _

}
