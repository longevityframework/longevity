package longevity.integration.withAssocSet

import longevity.IntegrationTest
import longevity.test.ScalaTestSpecs
import org.scalatest.Suites

@IntegrationTest
class MongoWithAssocSetSpec extends Suites(longevityContext.repoPoolSpec)
