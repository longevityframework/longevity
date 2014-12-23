package musette.repo.mongo

import org.scalatest._
import org.scalatest.OptionValues._
import longevity.testUtils.RepoSpec
import musette.domain.testUtils._
import musette.domain.Site

class MongoSiteRepoSpec extends RepoSpec[Site] {

  private val repoLayer = new MongoRepoLayer
  def ename = "site"
  def repo = repoLayer.siteRepo
  def genTestEntity = testEntityGen.site _
  def updateTestEntity = { e => e.copy(uri = e.uri + "77") }
  def persistedShouldMatchUnpersisted = entityMatchers.persistedSiteShouldMatchUnpersisted _

}


