package longevity.integration.subdomain.withSimpleConstraint

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class WithSimpleConstraintSpec extends Suites(
  context.mongoContext.inMemRepoCrudSpec,
  context.mongoContext.repoCrudSpec,
  context.cassandraContext.repoCrudSpec)

