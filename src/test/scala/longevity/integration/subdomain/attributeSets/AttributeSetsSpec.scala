package longevity.integration.subdomain.attributeSets

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class AttributeSetsSpec extends Suites(
  mongoContext.inMemRepoCrudSpec,
  mongoContext.repoCrudSpec,
  cassandraContext.repoCrudSpec)
