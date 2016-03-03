package longevity.integration.subdomain.keyWithAssoc

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class KeyWithAssocSpec extends Suites(
  context.mongoContext.inMemRepoCrudSpec,
  context.mongoContext.repoCrudSpec,
  context.cassandraContext.repoCrudSpec)
