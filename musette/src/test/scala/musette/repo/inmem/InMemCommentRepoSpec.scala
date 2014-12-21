package musette
package repo.inmem

import org.scalatest._
import org.scalatest.OptionValues._
import longevity.testUtils.InMemRepoSpec
import domain.testUtils._
import domain.Comment

class InMemCommentRepoSpec extends InMemRepoSpec[Comment] {

  private val repoLayer = new InMemRepoLayer
  def entityTypeName = "comment"
  def repo = repoLayer.commentRepo
  def genTestEntity = domain.testUtils.testEntityGen.comment _
  def updateTestEntity = { e => e.copy(uri = e.uri + "77") }
  def persistedShouldMatchUnpersisted = entityMatchers.persistedCommentShouldMatchUnpersisted _

}
