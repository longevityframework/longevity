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
  def testEntityGen = domain.testUtils.testEntityGen.comment _
  def persistedShouldMatchUnpersisted = entityMatchers.persistedCommentShouldMatchUnpersisted _

}


