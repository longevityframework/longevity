package longevity.integration.model.componentWithList

import org.scalatest.Suites
import longevity.integration.model.modelTestsExecutionContext

class WithComponentWithListSpec extends Suites(contexts.map(_.repoCrudSpec): _*)

