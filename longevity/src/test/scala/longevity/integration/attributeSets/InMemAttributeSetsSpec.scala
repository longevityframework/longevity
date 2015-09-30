package longevity.integration.attributeSets

import longevity.IntegrationTest

import org.scalatest.Suites

@IntegrationTest
class InMemAttributeSetsSpec extends Suites(context.longevityContext.inMemRepoPoolSpec)
