package musette.repo.inmem

import musette.domain.testUtil.entityMatchers
import musette.domain.Site
import musette.repo.MusetteRepoSpec

class InMemSiteRepoSpec extends MusetteRepoSpec[Site] {

  private val repoLayer = new InMemRepoLayer
  def ename = "site"
  def repo = repoLayer.siteRepo
  def persistedShouldMatchUnpersisted = entityMatchers.persistedSiteShouldMatchUnpersisted _

}


