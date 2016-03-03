package longevity.integration.subdomain.withComponentList

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class WithComponentListSpec extends Suites(
  context.mongoContext.inMemRepoCrudSpec,
  context.mongoContext.repoCrudSpec,
  context.cassandraContext.repoCrudSpec)
