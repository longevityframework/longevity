package longevity.integration.model.componentWithOption

import org.scalatest.Suites
import longevity.integration.model.modelTestsExecutionContext

class WithComponentWithOptionSpec extends Suites(contexts.map(_.repoCrudSpec): _*)

