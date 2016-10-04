package longevity.integration.subdomain.componentWithOption

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class WithComponentWithOptionSpec extends Suites(contexts.map(_.repoCrudSpec): _*)

