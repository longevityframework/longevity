package longevity.integration.attributeOptions

import longevity.IntegrationTest

import org.scalatest.Suites

@IntegrationTest
class InMemAttributeOptionsSpec extends Suites(longevityContext.inMemRepoPoolSpec)
