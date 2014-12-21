package musette
package repo.inmem

import org.scalatest._
import org.scalatest.OptionValues._
import longevity.testUtils.InMemRepoSpec
import domain.testUtils._
import domain.Wiki

class InMemWikiRepoSpec extends InMemRepoSpec[Wiki] {

  private val repoLayer = new InMemRepoLayer
  def entityTypeName = "wiki"
  def repo = repoLayer.wikiRepo
  def genTestEntity = domain.testUtils.testEntityGen.wiki _
  def updateTestEntity = { e => e.copy(uri = e.uri + "77") }
  def persistedShouldMatchUnpersisted = entityMatchers.persistedWikiShouldMatchUnpersisted _

}


