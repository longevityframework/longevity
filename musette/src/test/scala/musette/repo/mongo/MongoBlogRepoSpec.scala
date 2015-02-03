package musette.repo.mongo

import musette.domain.testUtil.entityMatchers
import musette.domain.Blog
import musette.repo.MusetteRepoSpec

class MongoBlogRepoSpec extends MusetteRepoSpec[Blog] {

  private val repoLayer = new MongoRepoLayer
  def ename = "blog"
  def repo = repoLayer.blogRepo
  def persistedShouldMatchUnpersisted = entityMatchers.persistedBlogShouldMatchUnpersisted _

}


