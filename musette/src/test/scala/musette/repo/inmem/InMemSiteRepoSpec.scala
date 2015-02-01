package musette.repo.inmem

import org.scalatest._
import org.scalatest.OptionValues._
import longevity.testUtil.RepoSpec
import musette.domain.testUtil._
import musette.domain.Site

class InMemSiteRepoSpec extends RepoSpec[Site] {

  private val repoLayer = new InMemRepoLayer
  def ename = "site"
  def repo = repoLayer.siteRepo
  def domainSpec = musette.domain.domainSpec
  def genTestEntity = testEntityGen.site _
  def updateTestEntity = { e => e.copy(uri = e.uri + "77") }
  def persistedShouldMatchUnpersisted = entityMatchers.persistedSiteShouldMatchUnpersisted _

}


