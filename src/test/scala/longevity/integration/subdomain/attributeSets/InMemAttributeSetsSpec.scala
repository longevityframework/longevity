package longevity.integration.subdomain.attributeSets

import org.scalatest.Suites

class InMemAttributeSetsSpec extends Suites(context.mongoContext.inMemRepoPoolSpec)
