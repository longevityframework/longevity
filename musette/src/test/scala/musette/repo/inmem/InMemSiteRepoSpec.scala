package musette
package repo.inmem

import org.scalatest._
import org.scalatest.OptionValues._
import longevity.testUtils.InMemRepoSpec
import domain.testUtils._
import domain.Site

class InMemSiteRepoSpec extends InMemRepoSpec[Site] {

  private val repoLayer = new InMemRepoLayer
  def entityTypeName = "site"
  def repo = repoLayer.siteRepo
  def testEntityGen = domain.testUtils.testEntityGen.site _
  def persistedShouldMatchUnpersisted = entityMatchers.persistedSiteShouldMatchUnpersisted _

}


