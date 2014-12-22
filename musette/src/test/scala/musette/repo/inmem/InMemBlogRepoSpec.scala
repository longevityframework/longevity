package musette.repo.inmem

import org.scalatest._
import org.scalatest.OptionValues._
import longevity.testUtils.RepoSpec
import musette.domain.testUtils._
import musette.domain.Blog

class InMemBlogRepoSpec extends RepoSpec[Blog] {

  private val repoLayer = new InMemRepoLayer
  def ename = "blog"
  def repo = repoLayer.blogRepo
  def genTestEntity = testEntityGen.blog _
  def updateTestEntity = { e => e.copy(uri = e.uri + "77") }
  def persistedShouldMatchUnpersisted = entityMatchers.persistedBlogShouldMatchUnpersisted _

}


