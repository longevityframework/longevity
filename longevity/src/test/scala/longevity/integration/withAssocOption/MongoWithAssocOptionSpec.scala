package longevity.integration.withAssocOption

import longevity.IntegrationTest

import org.scalatest.Suites

@IntegrationTest
class MongoWithAssocOptionSpec extends Suites(longevityContext.repoPoolSpec)
