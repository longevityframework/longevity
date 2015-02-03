package musette.repo.mongo

import musette.domain.testUtil.entityMatchers
import musette.domain.BlogPost
import musette.repo.MusetteRepoSpec

class MongoBlogPostRepoSpec extends MusetteRepoSpec[BlogPost] {

  private val repoLayer = new MongoRepoLayer
  def ename = "blog post"
  def repo = repoLayer.blogPostRepo
  def persistedShouldMatchUnpersisted = entityMatchers.persistedBlogPostShouldMatchUnpersisted _

}


