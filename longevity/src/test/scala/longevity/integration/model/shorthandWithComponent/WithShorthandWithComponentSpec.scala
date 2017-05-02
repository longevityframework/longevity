package longevity.integration.model.shorthandWithComponent

import org.scalatest.Suites
import longevity.integration.model.modelTestsExecutionContext

class WithShorthandWithComponentSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
