package longevity.integration.subdomain.attributeLists


import org.scalatest.Suites

class InMemAttributeListsSpec extends Suites(context.mongoContext.inMemRepoPoolSpec)
