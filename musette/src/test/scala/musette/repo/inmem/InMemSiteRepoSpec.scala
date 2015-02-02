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
  def domainConfig = musette.domain.domainConfig
  def persistedShouldMatchUnpersisted = entityMatchers.persistedSiteShouldMatchUnpersisted _

}


