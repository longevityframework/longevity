package longevity.integration.subdomain.withComponentOption

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class WithComponentOptionSpec extends Suites(
  context.mongoContext.inMemRepoCrudSpec,
  context.mongoContext.repoCrudSpec,
  context.cassandraContext.repoCrudSpec)
