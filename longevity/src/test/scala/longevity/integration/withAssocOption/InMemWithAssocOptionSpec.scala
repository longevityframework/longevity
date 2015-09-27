package longevity.integration.withAssocOption

import longevity.IntegrationTest

import org.scalatest.Suites

@IntegrationTest
class InMemWithAssocOptionSpec extends Suites(context.longevityContext.inMemRepoPoolSpec)
