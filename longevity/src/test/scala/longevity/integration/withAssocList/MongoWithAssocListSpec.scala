package longevity.integration.withAssocList

import longevity.IntegrationTest

import org.scalatest.Suites

@IntegrationTest
class MongoWithAssocListSpec extends Suites(context.longevityContext.repoPoolSpec)
