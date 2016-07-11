package longevity.integration.subdomain.complexConstraint

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class ComplexConstraintSpec extends Suites(
  mongoContext.inMemRepoCrudSpec,
  mongoContext.repoCrudSpec,
  cassandraContext.repoCrudSpec)

