package longevity.integration.model.componentWithOption

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class WithComponentWithOptionSpec extends Suites(contexts.map(_.repoCrudSpec): _*)

