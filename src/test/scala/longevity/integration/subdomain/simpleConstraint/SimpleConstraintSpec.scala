package longevity.integration.subdomain.simpleConstraint

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class SimpleConstraintSpec extends Suites(
  mongoContext.inMemRepoCrudSpec,
  mongoContext.repoCrudSpec,
  cassandraContext.repoCrudSpec)

