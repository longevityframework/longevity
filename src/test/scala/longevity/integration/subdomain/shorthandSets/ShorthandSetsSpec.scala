package longevity.integration.subdomain.shorthandSets

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class ShorthandSetsSpec extends Suites(
  mongoContext.inMemRepoCrudSpec,
  mongoContext.repoCrudSpec,
  cassandraContext.repoCrudSpec)
