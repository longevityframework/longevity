package musette.repo.inmem

import musette.domain.Blog
import musette.repo.MusetteRepoSpec

class InMemBlogRepoSpec extends MusetteRepoSpec[Blog] {

  private val repoLayer = new InMemRepoLayer
  def ename = "blog"
  def repo = repoLayer.blogRepo

}


