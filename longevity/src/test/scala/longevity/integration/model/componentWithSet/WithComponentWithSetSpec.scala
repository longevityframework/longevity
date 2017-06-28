package longevity.integration.model.componentWithSet

import org.scalatest.Suites

class WithComponentWithSetSpec extends Suites(contexts.map(_.repoCrudSpec): _*)

