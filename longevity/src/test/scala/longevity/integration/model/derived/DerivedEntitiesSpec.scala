package longevity.integration.model.derived

import org.scalatest.Suites
import longevity.integration.model.modelTestsExecutionContext

class DerivedEntitiesSpec extends Suites(contexts.map(_.repoCrudSpec): _*)

