package longevity.integration.subdomain.withSimpleConstraint

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class WithSimpleConstraintSpec extends Suites(
  mongoContext.inMemRepoCrudSpec,
  mongoContext.repoCrudSpec,
  cassandraContext.repoCrudSpec)

