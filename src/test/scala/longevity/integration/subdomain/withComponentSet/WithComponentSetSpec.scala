package longevity.integration.subdomain.withComponentSet

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class WithComponentSetSpec extends Suites(
  context.mongoContext.inMemRepoCrudSpec,
  context.mongoContext.repoCrudSpec,
  context.cassandraContext.repoCrudSpec)

