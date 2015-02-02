package musette.repo.mongo

import org.scalatest._
import org.scalatest.OptionValues._
import longevity.testUtil.RepoSpec
import musette.domain.testUtil._
import musette.domain.Wiki

class MongoWikiRepoSpec extends RepoSpec[Wiki] {

  private val repoLayer = new MongoRepoLayer
  def ename = "wiki"
  def repo = repoLayer.wikiRepo
  def domainConfig = musette.domain.domainConfig
  def persistedShouldMatchUnpersisted = entityMatchers.persistedWikiShouldMatchUnpersisted _

}


