package longevity.integration.subdomain.withComponent

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class WithComponentSpec extends Suites(
  context.mongoContext.inMemRepoCrudSpec,
  context.mongoContext.repoCrudSpec,
  context.cassandraContext.repoCrudSpec)

