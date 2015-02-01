package musette.repo.mongo

import org.scalatest._
import org.scalatest.OptionValues._
import longevity.testUtil.RepoSpec
import musette.domain.testUtil._
import musette.domain.BlogPost

class MongoBlogPostRepoSpec extends RepoSpec[BlogPost] {

  private val repoLayer = new MongoRepoLayer
  def ename = "blog post"
  def repo = repoLayer.blogPostRepo
  def domainConfig = musette.domain.domainConfig
  def genTestEntity = testEntityGen.blogPost _
  def updateTestEntity = { e => e.copy(uri = e.uri + "77") }
  def persistedShouldMatchUnpersisted = entityMatchers.persistedBlogPostShouldMatchUnpersisted _

}


