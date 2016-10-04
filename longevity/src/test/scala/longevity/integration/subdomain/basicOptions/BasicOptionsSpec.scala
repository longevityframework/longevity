package longevity.integration.subdomain.basicOptions

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class BasicOptionsSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
