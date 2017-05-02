package longevity.integration.model.component

import org.scalatest.Suites
import longevity.integration.model.modelTestsExecutionContext

class WithComponentSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
