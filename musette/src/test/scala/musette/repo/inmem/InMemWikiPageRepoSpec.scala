package musette.repo.inmem

import org.scalatest._
import org.scalatest.OptionValues._
import longevity.testUtils.InMemRepoSpec
import musette.domain.testUtils._
import musette.domain.WikiPage

class InMemWikiPageRepoSpec extends InMemRepoSpec[WikiPage] {

  private val repoLayer = new InMemRepoLayer
  def entityTypeName = "wiki page"
  def repo = repoLayer.wikiPageRepo
  def genTestEntity = testEntityGen.wikiPage _
  def updateTestEntity = { e => e.copy(uri = e.uri + "77") }
  def persistedShouldMatchUnpersisted = entityMatchers.persistedWikiPageShouldMatchUnpersisted _

}


