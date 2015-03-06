package longevity.integration.master

import longevity.testUtil.RepoPoolSpec
import longevity.IntegrationTest
import longevity.MasterIntegrationTest
import org.scalatest.Suites

@IntegrationTest
@MasterIntegrationTest
class MasterSuite extends Suites(
  new RepoPoolSpec(boundedContext, inMemRepoPool, suiteNameSuffix = Some("(InMem)")),
  new RepoPoolSpec(boundedContext, mongoRepoPool, suiteNameSuffix = Some("(Mongo)")))
