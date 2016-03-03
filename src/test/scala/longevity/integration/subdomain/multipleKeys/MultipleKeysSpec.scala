package longevity.integration.subdomain.multipleKeys

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class MultipleKeysSpec extends Suites(
  context.mongoContext.inMemRepoCrudSpec,
  context.mongoContext.repoCrudSpec,
  context.cassandraContext.repoCrudSpec)

