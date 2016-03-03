package longevity.integration.subdomain.attributeSets

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class AttributeSetsSpec extends Suites(
  context.mongoContext.inMemRepoCrudSpec,
  context.mongoContext.repoCrudSpec,
  context.cassandraContext.repoCrudSpec)
