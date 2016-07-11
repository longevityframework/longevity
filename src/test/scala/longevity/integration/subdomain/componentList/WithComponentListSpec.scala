package longevity.integration.subdomain.componentList

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class WithComponentListSpec extends Suites(
  mongoContext.inMemRepoCrudSpec,
  mongoContext.repoCrudSpec,
  cassandraContext.repoCrudSpec)
