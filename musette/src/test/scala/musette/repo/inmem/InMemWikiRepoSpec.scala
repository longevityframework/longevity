package musette.repo.inmem

import musette.domain.Wiki
import musette.repo.MusetteRepoSpec

class InMemWikiRepoSpec extends MusetteRepoSpec[Wiki] {

  private val repoLayer = new InMemRepoLayer
  def ename = "wiki"
  def repo = repoLayer.wikiRepo

}


