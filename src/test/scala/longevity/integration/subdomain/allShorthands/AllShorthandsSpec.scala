package longevity.integration.subdomain.allShorthands

import org.scalatest.Suites

class AllShorthandsSpec extends Suites(
  context.mongoContext.inMemRepoCrudSpec,
  context.mongoContext.repoCrudSpec)
  // context.mongoContext.repoCrudSpec,
  // context.cassandraContext.repoCrudSpec)
