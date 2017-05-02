package longevity.integration.model.shorthandOptions

import org.scalatest.Suites
import longevity.integration.model.modelTestsExecutionContext

class ShorthandOptionsSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
