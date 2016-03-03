package longevity.integration.subdomain.oneAttribute

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class OneAttributeSpec extends Suites(
  context.mongoContext.inMemRepoCrudSpec,
  context.mongoContext.repoCrudSpec,
  context.cassandraContext.repoCrudSpec)
