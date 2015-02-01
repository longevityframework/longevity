package musette.repo.inmem

import org.scalatest._
import org.scalatest.OptionValues._
import longevity.testUtil.RepoSpec
import musette.domain.testUtil._
import musette.domain.BlogPost

class InMemBlogPostRepoSpec extends RepoSpec[BlogPost] {

  private val repoLayer = new InMemRepoLayer
  def ename = "blog post"
  def repo = repoLayer.blogPostRepo
  def domainSpec = musette.domain.domainSpec
  def genTestEntity = testEntityGen.blogPost _
  def updateTestEntity = { e => e.copy(uri = e.uri + "77") }
  def persistedShouldMatchUnpersisted = entityMatchers.persistedBlogPostShouldMatchUnpersisted _

}


