package musette.repo.mongo

import org.scalatest._
import org.scalatest.OptionValues._
import longevity.testUtil.RepoSpec
import musette.domain.testUtil._
import musette.domain.WikiPage

class MongoWikiPageRepoSpec extends RepoSpec[WikiPage] {

  private val repoLayer = new MongoRepoLayer
  def ename = "wiki page"
  def repo = repoLayer.wikiPageRepo
  def domainConfig = musette.domain.domainConfig
  def persistedShouldMatchUnpersisted = entityMatchers.persistedWikiPageShouldMatchUnpersisted _

}


