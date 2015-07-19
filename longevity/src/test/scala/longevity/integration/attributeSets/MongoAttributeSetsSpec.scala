package longevity.integration.attributeSets

import longevity.IntegrationTest

import org.scalatest.Suites

@IntegrationTest
class MongoAttributeSetsSpec extends Suites(longevityContext.repoPoolSpec)
