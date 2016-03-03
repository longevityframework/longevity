package longevity.integration.subdomain.allAttributes

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class AllAttributesSpec extends Suites(
  context.mongoContext.inMemRepoCrudSpec,
  context.mongoContext.repoCrudSpec,
  context.cassandraContext.repoCrudSpec)
