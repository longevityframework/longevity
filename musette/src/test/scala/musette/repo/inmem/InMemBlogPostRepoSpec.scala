package musette
package repo.inmem

import org.scalatest._
import org.scalatest.OptionValues._
import longevity.testUtils.InMemRepoSpec
import domain.testUtils._
import domain.BlogPost

class InMemBlogPostRepoSpec extends InMemRepoSpec[BlogPost] {

  private val repoLayer = new InMemRepoLayer
  def entityTypeName = "blog post"
  def repo = repoLayer.blogPostRepo
  def testEntityGen = domain.testUtils.testEntityGen.blogPost _
  def persistedShouldMatchUnpersisted = entityMatchers.persistedBlogPostShouldMatchUnpersisted _

}


