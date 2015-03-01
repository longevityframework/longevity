package longevity.integration.master

import longevity.IntegrationTest
import longevity.MasterIntegrationTest
import org.scalatest._

@IntegrationTest
@MasterIntegrationTest
class MasterRepoLayerSpec extends FeatureSpec with GivenWhenThen with Matchers {

  feature("TODO") {

    scenario("attempt to add a repo for an entity type not yet represented in the entity pool") {
    }

  }

}
