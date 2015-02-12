package musette.repo.mongo

import musette.domain.Wiki
import musette.repo.MusetteRepoSpec

class MongoWikiRepoSpec extends MusetteRepoSpec[Wiki] {

  private val repoLayer = new MongoRepoLayer
  def ename = "wiki"
  def repo = repoLayer.wikiRepo

}


