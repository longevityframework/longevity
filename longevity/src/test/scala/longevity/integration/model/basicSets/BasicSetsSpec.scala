package longevity.integration.model.basicSets

import org.scalatest.Suites
import longevity.integration.model.modelTestsExecutionContext

class BasicSetsSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
