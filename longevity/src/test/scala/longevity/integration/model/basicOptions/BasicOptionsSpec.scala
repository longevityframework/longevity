package longevity.integration.model.basicOptions

import org.scalatest.Suites
import longevity.integration.model.modelTestsExecutionContext

class BasicOptionsSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
