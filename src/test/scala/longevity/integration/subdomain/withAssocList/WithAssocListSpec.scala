package longevity.integration.subdomain.withAssocList

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class WithAssocListSpec extends Suites(
  context.mongoContext.inMemRepoCrudSpec,
  context.mongoContext.repoCrudSpec,
  context.cassandraContext.repoCrudSpec)

