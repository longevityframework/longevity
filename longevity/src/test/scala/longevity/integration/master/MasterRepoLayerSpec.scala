package longevity.integration.master

import longevity.testUtil.RepoSpec
import longevity.IntegrationTest
import longevity.MasterIntegrationTest

@IntegrationTest
@MasterIntegrationTest
class MasterRepoLayerSpec extends RepoSpec[domain.User] {

  private val repoLayer = new longevity.integration.master.repo.InMemRepoLayer {}

  def boundedContext = domain.boundedContext
  def ename = "user"
  def repo = repoLayer.userRepo

}
