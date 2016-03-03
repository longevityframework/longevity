package longevity.integration.subdomain.withAssocOption

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class WithAssocOptionSpec extends Suites(
  context.mongoContext.inMemRepoCrudSpec,
  context.mongoContext.repoCrudSpec,
  context.cassandraContext.repoCrudSpec)
