package longevity.integration.model.componentOption

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class WithComponentOptionSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
