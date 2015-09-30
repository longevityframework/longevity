package longevity.integration.withComponentList

import longevity.IntegrationTest

import org.scalatest.Suites

@IntegrationTest
class MongoWithComponentListSpec extends Suites(context.longevityContext.repoPoolSpec)
