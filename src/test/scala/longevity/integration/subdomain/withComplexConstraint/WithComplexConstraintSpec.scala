package longevity.integration.subdomain.withComplexConstraint

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class WithComplexConstraintSpec extends Suites(
  context.mongoContext.inMemRepoCrudSpec,
  context.mongoContext.repoCrudSpec,
  context.cassandraContext.repoCrudSpec)

