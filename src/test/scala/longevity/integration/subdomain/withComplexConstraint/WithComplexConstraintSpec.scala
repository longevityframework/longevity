package longevity.integration.subdomain.withComplexConstraint

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class WithComplexConstraintSpec extends Suites(
  mongoContext.inMemRepoCrudSpec,
  mongoContext.repoCrudSpec,
  cassandraContext.repoCrudSpec)

