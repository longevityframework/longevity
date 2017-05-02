package longevity.integration.model.componentOption

import org.scalatest.Suites
import longevity.integration.model.modelTestsExecutionContext

class WithComponentOptionSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
