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
  def testEntityGen = domain.testUtils.testEntityGen.wiki _
  def persistedShouldMatchUnpersisted = entityMatchers.persistedWikiShouldMatchUnpersisted _

}


