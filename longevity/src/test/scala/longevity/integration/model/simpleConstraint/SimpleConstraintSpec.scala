package longevity.integration.model.simpleConstraint

import org.scalatest.Suites
import longevity.integration.model.modelTestsExecutionContext

class SimpleConstraintSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
