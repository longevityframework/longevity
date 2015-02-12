package musette.repo.mongo

import musette.domain.WikiPage
import musette.repo.MusetteRepoSpec

class MongoWikiPageRepoSpec extends MusetteRepoSpec[WikiPage] {

  private val repoLayer = new MongoRepoLayer
  def ename = "wiki page"
  def repo = repoLayer.wikiPageRepo

}


