package longevity.integration.attributeLists

import longevity.IntegrationTest

import org.scalatest.Suites

@IntegrationTest
class MongoAttributeListsSpec extends Suites(longevityContext.repoPoolSpec)
