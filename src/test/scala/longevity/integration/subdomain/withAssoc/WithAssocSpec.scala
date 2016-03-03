package longevity.integration.subdomain.withAssoc

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class WithAssocSpec extends Suites(
  context.mongoContext.inMemRepoCrudSpec,
  context.mongoContext.repoCrudSpec,
  context.cassandraContext.repoCrudSpec)
