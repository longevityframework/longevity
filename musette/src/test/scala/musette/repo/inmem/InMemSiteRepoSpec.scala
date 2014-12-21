package musette.repo.inmem

import org.scalatest._
import org.scalatest.OptionValues._
import longevity.testUtils.InMemRepoSpec
import musette.domain.testUtils._
import musette.domain.Site

class InMemSiteRepoSpec extends InMemRepoSpec[Site] {

  private val repoLayer = new InMemRepoLayer
  def entityTypeName = "site"
  def repo = repoLayer.siteRepo
  def genTestEntity = testEntityGen.site _
  def updateTestEntity = { e => e.copy(uri = e.uri + "77") }
  def persistedShouldMatchUnpersisted = entityMatchers.persistedSiteShouldMatchUnpersisted _

}


