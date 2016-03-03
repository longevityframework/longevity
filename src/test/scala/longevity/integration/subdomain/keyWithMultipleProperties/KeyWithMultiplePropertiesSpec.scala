package longevity.integration.subdomain.keyWithMultipleProperties

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class KeyWithMultiplePropertiesSpec extends Suites(
  context.mongoContext.inMemRepoCrudSpec,
  context.mongoContext.repoCrudSpec,
  context.cassandraContext.repoCrudSpec)
