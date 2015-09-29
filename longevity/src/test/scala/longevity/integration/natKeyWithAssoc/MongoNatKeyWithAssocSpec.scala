package longevity.integration.natKeyWithAssoc

import longevity.IntegrationTest

import org.scalatest.Suites

@IntegrationTest
class MongoNatKeyWithAssocSpec extends Suites(context.longevityContext.repoPoolSpec)
