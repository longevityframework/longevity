package longevity.integration.subdomain.withComponentOption

import org.scalatest.Suites

class InMemWithComponentOptionSpec extends Suites(context.mongoContext.inMemRepoPoolSpec)
