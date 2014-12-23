package musette.repo.mongo

import org.scalatest._
import org.scalatest.OptionValues._
import longevity.testUtils.RepoSpec
import musette.domain.testUtils._
import musette.domain.Wiki

class MongoWikiRepoSpec extends RepoSpec[Wiki] {

  private val repoLayer = new MongoRepoLayer
  def ename = "wiki"
  def repo = repoLayer.wikiRepo
  def genTestEntity = testEntityGen.wiki _
  def updateTestEntity = { e => e.copy(uri = e.uri + "77") }
  def persistedShouldMatchUnpersisted = entityMatchers.persistedWikiShouldMatchUnpersisted _

}


