package longevity.integration.model.componentShorthands

import org.scalatest.Suites
import longevity.integration.model.modelTestsExecutionContext

class WithComponentShorthandsSpec extends Suites(contexts.map(_.repoCrudSpec): _*)


