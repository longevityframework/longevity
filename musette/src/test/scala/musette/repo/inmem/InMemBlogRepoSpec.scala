package musette
package repo.inmem

import org.scalatest._
import org.scalatest.OptionValues._
import longevity.testUtils.InMemRepoSpec
import domain.testUtils._
import domain.Blog

class InMemBlogRepoSpec extends InMemRepoSpec[Blog] {

  private val repoLayer = new InMemRepoLayer
  def entityTypeName = "blog"
  def repo = repoLayer.blogRepo
  def testEntityGen = domain.testUtils.testEntityGen.blog _
  def persistedShouldMatchUnpersisted = entityMatchers.persistedBlogShouldMatchUnpersisted _

}


