package longevity.integration.model.key

import org.scalatest.Suites
import longevity.integration.model.modelTestsExecutionContext

class KeySpec extends Suites(contexts.map(_.repoCrudSpec): _*)

