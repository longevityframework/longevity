package longevity.integration.subdomain.allShorthands

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

/** covers a root entity with shorthands for every supported basic type */
class AllShorthandsSpec extends Suites(
  mongoContext.inMemRepoCrudSpec,
  mongoContext.repoCrudSpec,
  cassandraContext.repoCrudSpec)
