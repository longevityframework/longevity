package longevity.integration.natKeyWithMultipleProperties

import longevity.IntegrationTest

import org.scalatest.Suites

@IntegrationTest
class MongoNatKeyWithMultiplePropertiesSpec extends Suites(context.longevityContext.repoPoolSpec)
