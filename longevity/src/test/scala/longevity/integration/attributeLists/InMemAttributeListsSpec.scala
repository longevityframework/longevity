package longevity.integration.attributeLists

import longevity.IntegrationTest

import org.scalatest.Suites

@IntegrationTest
class InMemAttributeListsSpec extends Suites(context.longevityContext.inMemRepoPoolSpec)
