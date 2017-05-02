package longevity.integration.model.multipleKeys

import org.scalatest.Suites
import longevity.integration.model.modelTestsExecutionContext

class MultipleKeysSpec extends Suites(contexts.map(_.repoCrudSpec): _*)

