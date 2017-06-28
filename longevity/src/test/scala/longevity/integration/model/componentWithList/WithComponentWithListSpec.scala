package longevity.integration.model.componentWithList

import org.scalatest.Suites

class WithComponentWithListSpec extends Suites(contexts.map(_.repoCrudSpec): _*)

