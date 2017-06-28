package longevity.integration.model.simpleConstraint

import org.scalatest.Suites

class SimpleConstraintSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
