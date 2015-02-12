package musette.repo.inmem

import musette.domain.BlogPost
import musette.repo.MusetteRepoSpec

class InMemBlogPostRepoSpec extends MusetteRepoSpec[BlogPost] {

  private val repoLayer = new InMemRepoLayer
  def ename = "blog post"
  def repo = repoLayer.blogPostRepo

}


