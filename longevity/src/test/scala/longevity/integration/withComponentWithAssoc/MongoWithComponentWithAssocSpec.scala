package longevity.integration.withComponentWithAssoc

import longevity.IntegrationTest

import org.scalatest.Suites

@IntegrationTest
class MongoWithComponentWithAssocSpec extends Suites(longevityContext.repoPoolSpec)
