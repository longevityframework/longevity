package longevity.integration.withComponentSet


import org.scalatest.Suites

class InMemWithComponentSetSpec extends Suites(context.longevityContext.inMemRepoPoolSpec)
