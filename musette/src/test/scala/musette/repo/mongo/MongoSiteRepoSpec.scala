package musette.repo.mongo

import musette.domain.Site
import musette.repo.MusetteRepoSpec

class MongoSiteRepoSpec extends MusetteRepoSpec[Site] {

  private val repoLayer = new MongoRepoLayer
  def ename = "site"
  def repo = repoLayer.siteRepo

}


