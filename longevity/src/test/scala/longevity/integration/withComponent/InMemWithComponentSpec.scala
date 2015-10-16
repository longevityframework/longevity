package longevity.integration.withComponent


import org.scalatest.Suites

class InMemWithComponentSpec extends Suites(context.longevityContext.inMemRepoPoolSpec)
