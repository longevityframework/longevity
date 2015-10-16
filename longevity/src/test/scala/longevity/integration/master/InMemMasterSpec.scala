package longevity.integration.master


import org.scalatest.Suites

class InMemMasterSpec extends Suites(context.longevityContext.inMemRepoPoolSpec)
