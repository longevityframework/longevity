package longevity.integration.subdomain.oneShorthand

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class OneShorthandSpec extends Suites(
  context.mongoContext.inMemRepoCrudSpec,
  context.mongoContext.repoCrudSpec,
  context.cassandraContext.repoCrudSpec)

