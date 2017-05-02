package longevity.integration.model.complexConstraint

import org.scalatest.Suites
import longevity.integration.model.modelTestsExecutionContext

class ComplexConstraintSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
