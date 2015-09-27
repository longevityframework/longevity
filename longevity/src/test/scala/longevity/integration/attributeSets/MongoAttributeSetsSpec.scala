package longevity.integration.attributeSets

import longevity.IntegrationTest

import org.scalatest.Suites

@IntegrationTest
class MongoAttributeSetsSpec extends Suites(context.longevityContext.repoPoolSpec)
