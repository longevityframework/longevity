package musette.repo.inmem

import org.scalatest._
import org.scalatest.OptionValues._
import longevity.testUtils.InMemRepoSpec
import musette.domain.testUtils._
import musette.domain.BlogPost

class InMemBlogPostRepoSpec extends InMemRepoSpec[BlogPost] {

  private val repoLayer = new InMemRepoLayer
  def entityTypeName = "blog post"
  def repo = repoLayer.blogPostRepo
  def genTestEntity = testEntityGen.blogPost _
  def updateTestEntity = { e => e.copy(uri = e.uri + "77") }
  def persistedShouldMatchUnpersisted = entityMatchers.persistedBlogPostShouldMatchUnpersisted _

}


