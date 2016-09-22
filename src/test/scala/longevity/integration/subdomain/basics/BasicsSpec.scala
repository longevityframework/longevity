package longevity.integration.subdomain.basics

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class BasicsSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
