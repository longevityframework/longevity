package longevity.integration.subdomain.basics

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class BasicsSpec extends Suites(
  mongoContext.inMemRepoCrudSpec,
  mongoContext.repoCrudSpec,
  cassandraContext.repoCrudSpec)
