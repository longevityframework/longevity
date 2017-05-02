package longevity.integration.model.keyWithShorthand

import org.scalatest.Suites
import longevity.integration.model.modelTestsExecutionContext

class KeyWithShorthandSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
