package longevity.integration.model.componentWithSet

import org.scalatest.Suites
import longevity.integration.model.modelTestsExecutionContext

class WithComponentWithSetSpec extends Suites(contexts.map(_.repoCrudSpec): _*)

