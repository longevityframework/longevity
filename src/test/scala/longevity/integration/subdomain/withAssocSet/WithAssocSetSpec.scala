package longevity.integration.subdomain.withAssocSet

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class WithAssocSetSpec extends Suites(
  context.mongoContext.inMemRepoCrudSpec,
  context.mongoContext.repoCrudSpec,
  context.cassandraContext.repoCrudSpec)

