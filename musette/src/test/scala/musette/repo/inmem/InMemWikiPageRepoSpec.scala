package musette.repo.inmem

import musette.domain.testUtil.entityMatchers
import musette.domain.WikiPage
import musette.repo.MusetteRepoSpec

class InMemWikiPageRepoSpec extends MusetteRepoSpec[WikiPage] {

  private val repoLayer = new InMemRepoLayer
  def ename = "wiki page"
  def repo = repoLayer.wikiPageRepo
  def persistedShouldMatchUnpersisted = entityMatchers.persistedWikiPageShouldMatchUnpersisted _

}


