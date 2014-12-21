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
  def genTestEntity = domain.testUtils.testEntityGen.blog _
  def updateTestEntity = { e => e.copy(uri = e.uri + "77") }
  def persistedShouldMatchUnpersisted = entityMatchers.persistedBlogShouldMatchUnpersisted _

}


