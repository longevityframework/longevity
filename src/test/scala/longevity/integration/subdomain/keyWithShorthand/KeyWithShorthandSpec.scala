package longevity.integration.subdomain.keyWithShorthand

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class KeyWithShorthandSpec extends Suites(
  context.mongoContext.inMemRepoCrudSpec,
  context.mongoContext.repoCrudSpec,
  context.cassandraContext.repoCrudSpec)

