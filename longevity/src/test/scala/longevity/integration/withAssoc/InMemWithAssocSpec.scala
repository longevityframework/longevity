package longevity.integration.withAssoc

import longevity.IntegrationTest

import org.scalatest.Suites

@IntegrationTest
class InMemWithAssocSpec extends Suites(context.longevityContext.inMemRepoPoolSpec)
