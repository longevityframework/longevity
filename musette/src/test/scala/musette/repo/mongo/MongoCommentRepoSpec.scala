package musette.repo.mongo

import org.scalatest._
import org.scalatest.OptionValues._
import longevity.testUtil.RepoSpec
import musette.domain.testUtil._
import musette.domain.Comment

class MongoCommentRepoSpec extends RepoSpec[Comment] {

  private val repoLayer = new MongoRepoLayer
  def ename = "comment"
  def repo = repoLayer.commentRepo
  def domainConfig = musette.domain.domainConfig
  def persistedShouldMatchUnpersisted = entityMatchers.persistedCommentShouldMatchUnpersisted _

}
