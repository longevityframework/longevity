package longevity.integration.model.componentSet

import org.scalatest.Suites
import longevity.integration.model.modelTestsExecutionContext

class WithComponentSetSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
