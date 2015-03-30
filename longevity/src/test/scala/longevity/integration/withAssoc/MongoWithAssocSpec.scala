package longevity.integration.withAssoc

import longevity.IntegrationTest
import longevity.test.ScalaTestSpecs
import org.scalatest.Suites

@IntegrationTest
class MongoWithAssocSpec extends Suites(longevityContext.repoPoolSpec)
